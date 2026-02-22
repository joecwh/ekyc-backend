package com.auth.ekyc.ekyc_backend.domain.entity;

import com.auth.ekyc.ekyc_backend.domain.enums.KycStatus;
import com.auth.ekyc.ekyc_backend.domain.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_applications")
@Getter
@Setter
public class KycApplication extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus status;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    private Double riskScore;

    private String providerRefId;

    private LocalDateTime submittedAt;
    private LocalDateTime processingAt;
    private LocalDateTime completedAt;

    private String remark;

    @OneToOne(mappedBy = "kycApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private PersonalProfile personalProfile;

    @OneToMany(mappedBy = "kycApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<UserAddress> addresses = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "kycApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Attachment> attachments = new java.util.ArrayList<>();
}
