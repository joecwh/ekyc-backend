package com.auth.ekyc.ekyc_backend.application.dto.kyc;

import com.auth.ekyc.ekyc_backend.domain.enums.KycStatus;
import com.auth.ekyc.ekyc_backend.domain.enums.RiskLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDecisionRequest {

    @NotNull
    private KycStatus decision;

    @Size(max = 1000)
    private String remark;

    private RiskLevel riskLevel;
    private Double riskScore;

    @Size(max = 100)
    private String providerRefId;
}