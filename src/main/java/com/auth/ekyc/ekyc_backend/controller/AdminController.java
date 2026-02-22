package com.auth.ekyc.ekyc_backend.controller;

import com.auth.ekyc.ekyc_backend.application.dto.kyc.KycDecisionRequest;
import com.auth.ekyc.ekyc_backend.application.dto.kyc.KycSummaryDto;
import com.auth.ekyc.ekyc_backend.application.service.IKycService;
import com.auth.ekyc.ekyc_backend.domain.enums.KycStatus;
import com.auth.ekyc.ekyc_backend.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IKycService kycService;
    private final FileStorageService storageService;

    @GetMapping
    public List<KycSummaryDto> getByStatus(@RequestParam KycStatus status) {
        return kycService.getStatus(status);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> getFile(@RequestParam String path) {

        byte[] data = storageService.read(path);

        String contentType = "application/octet-stream";

        if (path.endsWith(".png")) contentType = "image/png";
        else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = "image/jpeg";
        else if (path.endsWith(".webp")) contentType = "image/webp";

        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(data);
    }

    @PostMapping("/kyc/ack/{kycId}")
    public void acknowledge(@PathVariable UUID kycId) {
        kycService.acknowledge(kycId);
    }

    @PostMapping("/kyc/decision/{kycId}")
    public void decide(
            @PathVariable UUID kycId,
            @RequestBody KycDecisionRequest request
    ) {
        kycService.decide(kycId, request);
    }

    @PostMapping("/kyc/manual-review/{kycId}")
    public void finalDecision(
            @PathVariable UUID kycId,
            @RequestBody KycDecisionRequest request
    ) {
        kycService.finalDecision(kycId, request);
    }
}
