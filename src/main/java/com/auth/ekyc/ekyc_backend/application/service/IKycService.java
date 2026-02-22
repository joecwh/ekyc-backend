package com.auth.ekyc.ekyc_backend.application.service;

import com.auth.ekyc.ekyc_backend.application.dto.kyc.KycDecisionRequest;
import com.auth.ekyc.ekyc_backend.application.dto.kyc.KycProfileRequest;
import com.auth.ekyc.ekyc_backend.application.dto.kyc.KycStartResponse;
import com.auth.ekyc.ekyc_backend.application.dto.kyc.KycSummaryDto;
import com.auth.ekyc.ekyc_backend.domain.enums.KycStatus;

import java.util.List;
import java.util.UUID;

public interface IKycService {

    KycStartResponse startKyc(UUID userId);
    void submit(UUID userId);
    void acknowledge(UUID kycId);
    void decide(UUID kycId, KycDecisionRequest request);
    void finalDecision(UUID kycId, KycDecisionRequest request);
    List<KycSummaryDto> getStatus(KycStatus status);
}