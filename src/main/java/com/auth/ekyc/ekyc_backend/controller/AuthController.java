package com.auth.ekyc.ekyc_backend.controller;

import com.auth.ekyc.ekyc_backend.application.service.IAuthService;
import com.auth.ekyc.ekyc_backend.application.dto.auth.LoginRequest;
import com.auth.ekyc.ekyc_backend.application.dto.auth.LoginResponse;
import com.auth.ekyc.ekyc_backend.application.dto.auth.RegisterRequest;
import com.auth.ekyc.ekyc_backend.application.dto.auth.RegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully. You can now login.");
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
