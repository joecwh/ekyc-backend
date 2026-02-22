package com.auth.ekyc.ekyc_backend.controller;

import com.auth.ekyc.ekyc_backend.application.dto.kyc.KycStartResponse;
import com.auth.ekyc.ekyc_backend.application.service.IAttachmentService;
import com.auth.ekyc.ekyc_backend.application.service.IKycService;
import com.auth.ekyc.ekyc_backend.common.exception.BusinessException;
import com.auth.ekyc.ekyc_backend.domain.enums.DocumentType;
import com.auth.ekyc.ekyc_backend.security.AuthUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {

    private final IKycService kycService;
    private final IAttachmentService attachmentService;

    @PostMapping("/start")
    public KycStartResponse startKyc(@AuthenticationPrincipal AuthUserPrincipal me) {
        return kycService.startKyc(me.userId());
    }

    @PostMapping("/upload")
    public void uploadMultiple(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("types") List<String> types
    ) {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new BusinessException("UNAUTHORIZED", "User not authenticated");
        }

        if (files.size() != types.size()) {
            throw new BusinessException("INVALID_REQUEST", "Files and types mismatch");
        }

        List<DocumentType> documentTypes = types.stream()
                .map(DocumentType::valueOf)
                .toList();

        attachmentService.uploadMultiple(principal.userId(), files, documentTypes);
    }

    @PostMapping("/submit")
    public void submitKyc(@AuthenticationPrincipal AuthUserPrincipal user) {
        kycService.submit(user.userId());
    }
}