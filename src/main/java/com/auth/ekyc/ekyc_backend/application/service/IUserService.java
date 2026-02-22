package com.auth.ekyc.ekyc_backend.application.service;

import com.auth.ekyc.ekyc_backend.application.dto.kyc.KycProfileRequest;
import com.auth.ekyc.ekyc_backend.application.dto.user.UserKycProfileResponse;
import com.auth.ekyc.ekyc_backend.application.dto.user.UserProfileResponse;

import java.util.UUID;

public interface IUserService {
    UserProfileResponse getCurrentUser(UUID userId);

    UserKycProfileResponse updateMyProfile(UUID userId, KycProfileRequest request);

    UserKycProfileResponse getMyProfile(UUID userId);
}
