package com.auth.ekyc.ekyc_backend.application.service;

import com.auth.ekyc.ekyc_backend.application.dto.auth.LoginRequest;
import com.auth.ekyc.ekyc_backend.application.dto.auth.LoginResponse;
import com.auth.ekyc.ekyc_backend.application.dto.auth.RegisterRequest;
import com.auth.ekyc.ekyc_backend.application.dto.auth.RegisterResponse;

public interface IAuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void verifyEmail(String token);
}
