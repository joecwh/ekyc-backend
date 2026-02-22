package com.auth.ekyc.ekyc_backend.application.dto.kyc;

import com.auth.ekyc.ekyc_backend.domain.enums.KycStatus;
import com.auth.ekyc.ekyc_backend.domain.enums.RiskLevel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycSummaryDto {
    private UUID kycId;

    private UUID userId;
    private String email;

    private KycStatus status;

    private RiskLevel riskLevel;
    private Double riskScore;

    private LocalDateTime submittedAt;
    private LocalDateTime processingAt;
    private LocalDateTime completedAt;

    private String remark;

    private Integer attachmentsCount;

    private List<AttachmentSummaryDto> attachments;
}