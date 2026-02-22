package com.auth.ekyc.ekyc_backend.application.dto.user;

import com.auth.ekyc.ekyc_backend.domain.enums.AddressType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddressDto {

    private AddressType addressType;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postcode;
    private String state;
    private String country;
}