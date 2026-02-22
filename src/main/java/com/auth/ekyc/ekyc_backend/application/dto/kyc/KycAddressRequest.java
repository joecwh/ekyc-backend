package com.auth.ekyc.ekyc_backend.application.dto.kyc;

import com.auth.ekyc.ekyc_backend.domain.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KycAddressRequest {

    @NotNull
    private AddressType addressType;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String city;

    @NotBlank
    private String postcode;

    @NotBlank
    private String state;

    @NotBlank
    private String country;
}