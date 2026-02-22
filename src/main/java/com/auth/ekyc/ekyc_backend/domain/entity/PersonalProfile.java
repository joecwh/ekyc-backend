package com.auth.ekyc.ekyc_backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "personal_profiles")
@Getter
@Setter
public class PersonalProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kyc_id", nullable = false, unique = true)
    private KycApplication kycApplication;

    private String firstName;
    private String lastName;
    private String idNumber;
    private LocalDate dob;
    private String nationality;
    private String occupation;
    private Boolean isPep;
}