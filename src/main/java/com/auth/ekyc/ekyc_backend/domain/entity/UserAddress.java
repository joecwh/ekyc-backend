package com.auth.ekyc.ekyc_backend.domain.entity;

import com.auth.ekyc.ekyc_backend.domain.enums.AddressType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
public class UserAddress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kyc_id", nullable = false)
    private KycApplication kycApplication;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postcode;
    private String state;
    private String country;
}