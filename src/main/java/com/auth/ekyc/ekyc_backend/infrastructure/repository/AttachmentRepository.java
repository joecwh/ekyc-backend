package com.auth.ekyc.ekyc_backend.infrastructure.repository;

import com.auth.ekyc.ekyc_backend.domain.entity.Attachment;
import com.auth.ekyc.ekyc_backend.domain.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    List<Attachment> findByKycApplicationId(UUID kycId);

    Optional<Attachment> findByKycApplicationIdAndDocumentType(UUID kycId, DocumentType type);

    boolean existsByKycApplicationIdAndDocumentType(UUID kycId, DocumentType type);

    long countByKycApplicationId(UUID kycId);
}