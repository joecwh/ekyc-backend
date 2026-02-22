package com.auth.ekyc.ekyc_backend.application.dto.user;

import com.auth.ekyc.ekyc_backend.domain.enums.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserKycProfileResponse {

    private UUID kycId;
    private KycStatus status;

    private String firstName;
    private String lastName;
    private String idNumber;
    private LocalDate dob;
    private String nationality;
    private String occupation;
    private Boolean isPep;

    private List<AddressDto> addresses;
}