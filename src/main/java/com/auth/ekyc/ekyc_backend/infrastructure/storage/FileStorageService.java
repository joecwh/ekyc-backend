package com.auth.ekyc.ekyc_backend.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String save(String folder, MultipartFile file);

    void delete(String path);

    byte[] read(String path);
}