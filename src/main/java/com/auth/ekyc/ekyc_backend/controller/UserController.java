package com.auth.ekyc.ekyc_backend.controller;

import com.auth.ekyc.ekyc_backend.application.dto.kyc.KycProfileRequest;
import com.auth.ekyc.ekyc_backend.application.dto.user.UserKycProfileResponse;
import com.auth.ekyc.ekyc_backend.application.dto.user.UserProfileResponse;
import com.auth.ekyc.ekyc_backend.application.service.IKycService;
import com.auth.ekyc.ekyc_backend.application.service.IUserService;
import com.auth.ekyc.ekyc_backend.security.AuthUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final IKycService kycService;

    @GetMapping
    public UserProfileResponse me(@AuthenticationPrincipal AuthUserPrincipal me) {
        return userService.getCurrentUser(me.userId());
    }

    @PutMapping("/profile")
    public UserKycProfileResponse updateProfile(
            @AuthenticationPrincipal AuthUserPrincipal me,
            @Valid @RequestBody KycProfileRequest request) {

        kycService.startKyc(me.userId());
        return userService.updateMyProfile(me.userId(), request);
    }

    @GetMapping("/profile")
    public UserKycProfileResponse getProfile(@AuthenticationPrincipal AuthUserPrincipal me) {
        return userService.getMyProfile(me.userId());
    }
}