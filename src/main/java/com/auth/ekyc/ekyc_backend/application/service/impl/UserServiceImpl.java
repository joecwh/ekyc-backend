package com.auth.ekyc.ekyc_backend.application.service.impl;

import com.auth.ekyc.ekyc_backend.application.dto.kyc.KycAddressRequest;
import com.auth.ekyc.ekyc_backend.application.dto.kyc.KycProfileRequest;
import com.auth.ekyc.ekyc_backend.application.dto.user.AddressDto;
import com.auth.ekyc.ekyc_backend.application.dto.user.UserKycProfileResponse;
import com.auth.ekyc.ekyc_backend.application.dto.user.UserProfileResponse;
import com.auth.ekyc.ekyc_backend.application.service.IUserService;
import com.auth.ekyc.ekyc_backend.common.exception.BusinessException;
import com.auth.ekyc.ekyc_backend.domain.entity.KycApplication;
import com.auth.ekyc.ekyc_backend.domain.entity.PersonalProfile;
import com.auth.ekyc.ekyc_backend.domain.entity.User;
import com.auth.ekyc.ekyc_backend.domain.entity.UserAddress;
import com.auth.ekyc.ekyc_backend.domain.enums.KycStatus;
import com.auth.ekyc.ekyc_backend.infrastructure.repository.KycApplicationRepository;
import com.auth.ekyc.ekyc_backend.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final KycApplicationRepository kycRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        KycStatus status = KycStatus.DRAFT;

        KycApplication kyc = kycRepository.findByUserId(userId).orElse(null);
        if (kyc != null)
            status = kyc.getStatus();

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getEmailVerified(),
                user.getAccountStatus(),
                status
        );
    }

    @Override
    @Transactional
    public UserKycProfileResponse updateMyProfile(UUID userId, KycProfileRequest req) {

        KycApplication kyc = kycRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("KYC_NOT_FOUND", "Start KYC first"));

        if (kyc.getStatus() != KycStatus.DRAFT && kyc.getStatus() != KycStatus.RETRY_REQUIRED)
            throw new BusinessException("KYC_LOCKED", "KYC cannot be edited");

        // PROFILE
        PersonalProfile profile = kyc.getPersonalProfile();
        if (profile == null) {
            profile = new PersonalProfile();
            profile.setKycApplication(kyc);
            kyc.setPersonalProfile(profile);
        }

        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        profile.setIdNumber(req.getIdNumber());
        profile.setDob(req.getDob());
        profile.setNationality(req.getNationality());
        profile.setOccupation(req.getOccupation());
        profile.setIsPep(req.getIsPep());

        // ADDRESSES
        kyc.getAddresses().clear();

        for (KycAddressRequest a : req.getAddresses()) {
            UserAddress addr = new UserAddress();
            addr.setKycApplication(kyc);
            addr.setAddressType(a.getAddressType());
            addr.setAddressLine1(a.getAddressLine1());
            addr.setAddressLine2(a.getAddressLine2());
            addr.setCity(a.getCity());
            addr.setPostcode(a.getPostcode());
            addr.setState(a.getState());
            addr.setCountry(a.getCountry());
            kyc.getAddresses().add(addr);
        }

        return mapToResponse(kyc);
    }

    @Override
    @Transactional(readOnly = true)
    public UserKycProfileResponse getMyProfile(UUID userId) {

        KycApplication kyc = kycRepository.findByUserId(userId).orElse(null);

        // User never started KYC yet
        if (kyc == null) {
            return new UserKycProfileResponse(
                    null,
                    KycStatus.DRAFT,
                    null, null, null, null, null, null, null,
                    List.of()
            );
        }

        return mapToResponse(kyc);
    }

    private UserKycProfileResponse mapToResponse(KycApplication kyc) {

        PersonalProfile p = kyc.getPersonalProfile();

        List<AddressDto> addresses = kyc.getAddresses().stream()
                .map(a -> new AddressDto(
                        a.getAddressType(),
                        a.getAddressLine1(),
                        a.getAddressLine2(),
                        a.getCity(),
                        a.getPostcode(),
                        a.getState(),
                        a.getCountry()
                ))
                .toList();

        return new UserKycProfileResponse(
                kyc.getId(),
                kyc.getStatus(),
                p != null ? p.getFirstName() : null,
                p != null ? p.getLastName() : null,
                p != null ? p.getIdNumber() : null,
                p != null ? p.getDob() : null,
                p != null ? p.getNationality() : null,
                p != null ? p.getOccupation() : null,
                p != null ? p.getIsPep() : null,
                addresses
        );
    }
}
