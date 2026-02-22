package com.auth.ekyc.ekyc_backend.application.dto.user;

import com.auth.ekyc.ekyc_backend.domain.enums.AccountStatus;
import com.auth.ekyc.ekyc_backend.domain.enums.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String email;
    private boolean emailVerified;
    private AccountStatus accountStatus;
    private KycStatus kycStatus;
}