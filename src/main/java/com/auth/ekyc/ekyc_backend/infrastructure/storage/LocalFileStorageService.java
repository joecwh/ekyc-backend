package com.auth.ekyc.ekyc_backend.infrastructure.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path root = Paths.get("uploads");

    @Override
    public String save(String folder, MultipartFile file) {

        try {
            Path dir = root.resolve(folder);
            Files.createDirectories(dir);

            String ext = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;

            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return folder + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("FILE_SAVE_FAILED");
        }
    }

    @Override
    public void delete(String path) {
        try {
            Files.deleteIfExists(root.resolve(path));
        } catch (IOException ignored) {}
    }

    @Override
    public byte[] read(String path) {
        try {
            return Files.readAllBytes(root.resolve(path));
        } catch (IOException e) {
            throw new RuntimeException("FILE_NOT_FOUND");
        }
    }

    private String getExtension(String name) {
        if (name == null || !name.contains(".")) return "";
        return name.substring(name.lastIndexOf("."));
    }
}