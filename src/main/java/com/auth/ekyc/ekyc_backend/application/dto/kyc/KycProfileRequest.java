package com.auth.ekyc.ekyc_backend.application.dto.kyc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class KycProfileRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String idNumber;

    @NotNull
    private LocalDate dob;

    @NotBlank
    private String nationality;

    private String occupation;
    private Boolean isPep;

    @NotEmpty
    private List<KycAddressRequest> addresses;
}