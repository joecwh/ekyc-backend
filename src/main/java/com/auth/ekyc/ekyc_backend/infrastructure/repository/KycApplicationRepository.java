package com.auth.ekyc.ekyc_backend.infrastructure.repository;

import com.auth.ekyc.ekyc_backend.domain.entity.KycApplication;
import com.auth.ekyc.ekyc_backend.domain.enums.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KycApplicationRepository extends JpaRepository<KycApplication, UUID> {

    List<KycApplication> findByStatus(KycStatus status);
    Optional<KycApplication> findByUserId(UUID userId);
}