package com.auth.ekyc.ekyc_backend.application.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RegisterResponse {
    private UUID userId;
    private String email;
    private String message;
}
