package com.auth.ekyc.ekyc_backend.application.service;

import com.auth.ekyc.ekyc_backend.domain.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IAttachmentService {

    void upload(UUID userId, DocumentType type, MultipartFile file);

    void uploadMultiple(UUID userId, List<MultipartFile> files, List<DocumentType> types);
}