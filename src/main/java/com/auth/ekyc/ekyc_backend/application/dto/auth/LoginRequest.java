package com.auth.ekyc.ekyc_backend.application.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
