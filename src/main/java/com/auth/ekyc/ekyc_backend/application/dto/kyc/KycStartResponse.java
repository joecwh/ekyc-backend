package com.auth.ekyc.ekyc_backend.application.dto.kyc;

import com.auth.ekyc.ekyc_backend.domain.enums.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class KycStartResponse {
    private UUID kycId;
    private KycStatus status;
}