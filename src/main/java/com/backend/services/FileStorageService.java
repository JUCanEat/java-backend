package com.backend.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.menu-images.temp-storage-path:./temp/menus}")
    private String tempStoragePath;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(tempStoragePath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create temp directory", e);
        }
    }

    public String storeTempMenuImage(MultipartFile file, UUID menuId) {
        try {
            String filename = menuId + "_" + System.currentTimeMillis() +
                    getFileExtension(file.getOriginalFilename());
            String fullPath = tempStoragePath + "/" + filename;

            Path targetLocation = Paths.get(fullPath);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fullPath;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store temp file", e);
        }
    }

    public void deleteTempFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            //log.error("Failed to delete temp file: {}", filePath, e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) return ".jpg";
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : ".jpg";
    }
}