package com.auth.ekyc.ekyc_backend.application.dto.kyc;

import com.auth.ekyc.ekyc_backend.domain.enums.DocumentType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentSummaryDto {

    private UUID attachmentId;
    private DocumentType documentType;
    private String filePath;
    private String mimeType;
    private Long fileSize;
}