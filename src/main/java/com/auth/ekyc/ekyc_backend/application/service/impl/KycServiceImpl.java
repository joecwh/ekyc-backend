package com.auth.ekyc.ekyc_backend.application.service.impl;

import com.auth.ekyc.ekyc_backend.application.dto.kyc.*;
import com.auth.ekyc.ekyc_backend.application.service.IKycService;
import com.auth.ekyc.ekyc_backend.common.exception.BusinessException;
import com.auth.ekyc.ekyc_backend.domain.entity.KycApplication;
import com.auth.ekyc.ekyc_backend.domain.entity.PersonalProfile;
import com.auth.ekyc.ekyc_backend.domain.entity.User;
import com.auth.ekyc.ekyc_backend.domain.entity.UserAddress;
import com.auth.ekyc.ekyc_backend.domain.enums.DocumentType;
import com.auth.ekyc.ekyc_backend.domain.enums.KycStatus;
import com.auth.ekyc.ekyc_backend.infrastructure.repository.AttachmentRepository;
import com.auth.ekyc.ekyc_backend.infrastructure.repository.KycApplicationRepository;
import com.auth.ekyc.ekyc_backend.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KycServiceImpl implements IKycService {

    private final KycApplicationRepository kycRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;

    @Override
    @Transactional
    public KycStartResponse startKyc(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        KycApplication kyc = kycRepository.findByUserId(userId).orElse(null);

        // First time
        if (kyc == null) {
            kyc = new KycApplication();
            kyc.setUser(user);
            kyc.setStatus(KycStatus.DRAFT);
            kyc.setCreatedAt(LocalDateTime.now());
            kycRepository.save(kyc);

            return new KycStartResponse(kyc.getId(), kyc.getStatus());
        }

        // Retry flow
        if (kyc.getStatus() == KycStatus.RETRY_REQUIRED) {
            kyc.setStatus(KycStatus.DRAFT);
            kyc.setUpdatedAt(LocalDateTime.now());
            return new KycStartResponse(kyc.getId(), kyc.getStatus());
        }

        // Allowed resume
        if (kyc.getStatus() == KycStatus.DRAFT) {
            return new KycStartResponse(kyc.getId(), kyc.getStatus());
        }

        // Block states
        throw new BusinessException(
                "KYC_NOT_ALLOWED",
                "KYC cannot be started. Current status: " + kyc.getStatus()
        );
    }

    @Override
    @Transactional
    public void submit(UUID userId) {

        KycApplication kyc = kycRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("KYC_NOT_FOUND", "Start KYC first"));

        if (kyc.getStatus() != KycStatus.DRAFT)
            throw new BusinessException("INVALID_STATUS", "KYC already submitted");

        validateBeforeSubmit(kyc);

        kyc.setStatus(KycStatus.SUBMITTED);
        kyc.setSubmittedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void acknowledge(UUID kycId) {

        KycApplication kyc = kycRepository.findById(kycId)
                .orElseThrow(() -> new BusinessException("KYC_NOT_FOUND", "KYC not found"));

        if (kyc.getStatus() != KycStatus.SUBMITTED)
            throw new BusinessException("INVALID_STATUS", "Not in submitted state");

        kyc.setStatus(KycStatus.PROCESSING);
        kyc.setProcessingAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void decide(UUID kycId, KycDecisionRequest request) {

        KycApplication kyc = kycRepository.findById(kycId)
                .orElseThrow(() -> new BusinessException("KYC_NOT_FOUND", "KYC not found"));

        if (kyc.getStatus() != KycStatus.PROCESSING &&
                kyc.getStatus() != KycStatus.MANUAL_REVIEW)
            throw new BusinessException("INVALID_STATUS", "KYC not in review state");

        KycStatus decision = request.getDecision();

        switch (decision) {

            case MANUAL_REVIEW:
                kyc.setStatus(KycStatus.MANUAL_REVIEW);
                break;

            case APPROVED:
                kyc.setStatus(KycStatus.APPROVED);
                kyc.setCompletedAt(LocalDateTime.now());
                break;

            case REJECTED:
                requireRemark(request.getRemark());
                kyc.setStatus(KycStatus.REJECTED);
                kyc.setCompletedAt(LocalDateTime.now());
                break;

            case RETRY_REQUIRED:
                requireRemark(request.getRemark());
                kyc.setStatus(KycStatus.RETRY_REQUIRED);
                kyc.setCompletedAt(LocalDateTime.now());
                break;

            default:
                throw new BusinessException("INVALID_DECISION", "Invalid decision status");
        }

        kyc.setRemark(request.getRemark());
        kyc.setRiskLevel(request.getRiskLevel());
        kyc.setRiskScore(request.getRiskScore());
        kyc.setProviderRefId(request.getProviderRefId());
    }

    @Override
    @Transactional
    public void finalDecision(UUID kycId, KycDecisionRequest request) {

        KycApplication kyc = kycRepository.findById(kycId)
                .orElseThrow(() -> new BusinessException("KYC_NOT_FOUND", "KYC not found"));

        if (kyc.getStatus() != KycStatus.MANUAL_REVIEW)
            throw new BusinessException("INVALID_STATUS", "KYC not in manual review state");

        KycStatus decision = request.getDecision();

        if (decision != KycStatus.APPROVED &&
                decision != KycStatus.REJECTED &&
                decision != KycStatus.RETRY_REQUIRED)
            throw new BusinessException("INVALID_DECISION", "Manual review must end with final status");

        if (decision != KycStatus.APPROVED)
            requireRemark(request.getRemark());

        kyc.setStatus(decision);
        kyc.setCompletedAt(LocalDateTime.now());
        kyc.setRemark(request.getRemark());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KycSummaryDto> getStatus(KycStatus status) {

        List<KycApplication> list = kycRepository.findByStatus(status);

        return list.stream()
                .map(this::toSummaryDto)
                .toList();
    }

    private KycSummaryDto toSummaryDto(KycApplication kyc) {

        List<AttachmentSummaryDto> attachmentDtos =
                kyc.getAttachments().stream()
                        .map(att -> AttachmentSummaryDto.builder()
                                .attachmentId(att.getId())
                                .documentType(att.getDocumentType())
                                .filePath(att.getFilePath())
                                .mimeType(att.getMimeType())
                                .fileSize(att.getFileSize())
                                .build())
                        .toList();

        return KycSummaryDto.builder()
                .kycId(kyc.getId())
                .userId(kyc.getUser().getId())
                .email(kyc.getUser().getEmail())
                .status(kyc.getStatus())
                .riskLevel(kyc.getRiskLevel())
                .riskScore(kyc.getRiskScore())
                .submittedAt(kyc.getSubmittedAt())
                .processingAt(kyc.getProcessingAt())
                .completedAt(kyc.getCompletedAt())
                .attachmentsCount(attachmentDtos.size())
                .attachments(attachmentDtos)
                .remark(kyc.getRemark())
                .build();
    }

    private void validateBeforeSubmit(KycApplication kyc) {

        PersonalProfile profile = kyc.getPersonalProfile();

        if (profile == null) {
            throw new BusinessException("MISSING_PROFILE", "Personal profile not completed");
        }

        if (isBlank(profile.getFirstName()))
            throw new BusinessException("MISSING_FIRST_NAME", "First name is required");

        if (isBlank(profile.getLastName()))
            throw new BusinessException("MISSING_LAST_NAME", "Last name is required");

        if (isBlank(profile.getIdNumber()))
            throw new BusinessException("MISSING_ID_NUMBER", "ID number is required");

        if (profile.getDob() == null)
            throw new BusinessException("MISSING_DOB", "Date of birth is required");

        if (isBlank(profile.getNationality()))
            throw new BusinessException("MISSING_NATIONALITY", "Nationality is required");

        if (isBlank(profile.getOccupation()))
            throw new BusinessException("MISSING_OCCUPATION", "Occupation is required");

        if (kyc.getAddresses() == null || kyc.getAddresses().isEmpty()) {
            throw new BusinessException("MISSING_ADDRESS", "At least one address is required");
        }

        for (UserAddress addr : kyc.getAddresses()) {

            if (isBlank(addr.getAddressLine1()))
                throw new BusinessException("MISSING_ADDRESS_LINE1", "Address Line 1 is required");

            if (isBlank(addr.getCity()))
                throw new BusinessException("MISSING_CITY", "City is required");

            if (isBlank(addr.getState()))
                throw new BusinessException("MISSING_STATE", "State is required");

            if (isBlank(addr.getPostcode()))
                throw new BusinessException("MISSING_POSTCODE", "Postcode is required");

            if (isBlank(addr.getCountry()))
                throw new BusinessException("MISSING_COUNTRY", "Country is required");
        }

        if (!attachmentRepository.existsByKycApplicationIdAndDocumentType(kyc.getId(), DocumentType.NRIC_FRONT))
            throw new BusinessException("MISSING_IC_FRONT", "IC Front required");

        if (!attachmentRepository.existsByKycApplicationIdAndDocumentType(kyc.getId(), DocumentType.NRIC_BACK))
            throw new BusinessException("MISSING_IC_BACK", "IC Back required");

        if (!attachmentRepository.existsByKycApplicationIdAndDocumentType(kyc.getId(), DocumentType.SELFIE))
            throw new BusinessException("MISSING_SELFIE", "Selfie required");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void requireRemark(String remark) {
        if (remark == null || remark.trim().isEmpty()) {
            throw new BusinessException("REMARK_REQUIRED", "Remark is required for this decision");
        }
    }
}