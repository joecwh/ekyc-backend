package com.auth.ekyc.ekyc_backend.domain.entity;

import com.auth.ekyc.ekyc_backend.domain.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "attachments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"kyc_id", "document_type"}))
@Getter
@Setter
public class Attachment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kyc_id", nullable = false)
    private KycApplication kycApplication;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String filePath;
    private String fileHash;
    private String mimeType;
    private Long fileSize;
}