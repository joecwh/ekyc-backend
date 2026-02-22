package com.auth.ekyc.ekyc_backend.application.service.impl;

import com.auth.ekyc.ekyc_backend.application.service.IAttachmentService;
import com.auth.ekyc.ekyc_backend.common.exception.BusinessException;
import com.auth.ekyc.ekyc_backend.domain.entity.Attachment;
import com.auth.ekyc.ekyc_backend.domain.entity.KycApplication;
import com.auth.ekyc.ekyc_backend.domain.enums.DocumentType;
import com.auth.ekyc.ekyc_backend.infrastructure.repository.AttachmentRepository;
import com.auth.ekyc.ekyc_backend.infrastructure.repository.KycApplicationRepository;
import com.auth.ekyc.ekyc_backend.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements IAttachmentService {

    private final KycApplicationRepository kycRepository;
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService storageService;

    @Override
    @Transactional
    public void upload(UUID userId, DocumentType type, MultipartFile file) {

        KycApplication kyc = kycRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("KYC_NOT_STARTED", "Start KYC first"));

        validateFile(file);

        // Replace existing file
        attachmentRepository.findByKycApplicationIdAndDocumentType(kyc.getId(), type)
                .ifPresent(old -> {
                    storageService.delete(old.getFilePath());
                    attachmentRepository.delete(old);
                });

        String path = storageService.save("kyc/" + userId, file);

        Attachment att = new Attachment();
        att.setKycApplication(kyc);
        att.setDocumentType(type);
        att.setFilePath(path);
        att.setMimeType(file.getContentType());
        att.setFileSize(file.getSize());
        att.setFileHash(hash(file));

        attachmentRepository.save(att);
    }

    @Override
    @Transactional
    public void uploadMultiple(UUID userId,
                               List<MultipartFile> files,
                               List<DocumentType> types) {

        KycApplication kyc = kycRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("KYC_NOT_STARTED", "Start KYC first"));

        for (int i = 0; i < files.size(); i++) {

            MultipartFile file = files.get(i);
            DocumentType type = types.get(i);

            validateFile(file);

            // replace old if exists
            attachmentRepository.findByKycApplicationIdAndDocumentType(kyc.getId(), type)
                    .ifPresent(old -> {
                        storageService.delete(old.getFilePath());
                        attachmentRepository.delete(old);
                    });

            String path = storageService.save("kyc/" + userId, file);

            Attachment att = new Attachment();
            att.setKycApplication(kyc);
            att.setDocumentType(type);
            att.setFilePath(path);
            att.setMimeType(file.getContentType());
            att.setFileSize(file.getSize());
            att.setFileHash(hash(file));

            attachmentRepository.save(att);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty())
            throw new BusinessException("FILE_EMPTY", "File cannot be empty");

        if (file.getSize() > 5 * 1024 * 1024)
            throw new BusinessException("FILE_TOO_LARGE", "Max 5MB allowed");

        if (!file.getContentType().startsWith("image/"))
            throw new BusinessException("INVALID_FILE_TYPE", "Only images allowed");
    }

    private String hash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return null;
        }
    }
}