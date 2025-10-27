package com.hss.hss_backend.controller;

import com.hss.hss_backend.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.cloud.gcp.storage.enabled", havingValue = "true", matchIfMissing = false)
public class FileController {

    private final StorageService storageService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) {
        log.info("Uploading file: {} to folder: {}", file.getOriginalFilename(), folder);

        try {
            String fileUrl = storageService.uploadFile(file, folder);

            Map<String, String> response = new HashMap<>();
            response.put("fileUrl", fileUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", String.valueOf(file.getSize()));
            response.put("contentType", file.getContentType());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("filePath") String filePath) {
        log.info("Downloading file: {}", filePath);

        try {
            byte[] fileContent = storageService.downloadFile(filePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);
        } catch (Exception e) {
            log.error("Failed to download file: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/signed-url")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, String>> generateSignedUrl(
            @RequestParam("filePath") String filePath,
            @RequestParam(defaultValue = "3600000") long expirationTime) {
        log.info("Generating signed URL for file: {}", filePath);

        try {
            String signedUrl = storageService.generateSignedUrl(filePath, expirationTime);

            Map<String, String> response = new HashMap<>();
            response.put("signedUrl", signedUrl);
            response.put("expirationTime", String.valueOf(expirationTime));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to generate signed URL: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<Void> deleteFile(@RequestParam("filePath") String filePath) {
        log.info("Deleting file: {}", filePath);

        try {
            storageService.deleteFile(filePath);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete file: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Boolean>> fileExists(@RequestParam("filePath") String filePath) {
        log.info("Checking if file exists: {}", filePath);

        boolean exists = storageService.fileExists(filePath);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }
}
