package com.hss.hss_backend.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    @Value("${spring.cloud.gcp.storage.bucket-name}")
    private String bucketName;

    private final Storage storage;

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        log.info("Uploading file: {} to folder: {}", file.getOriginalFilename(), folder);
        
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String filePath = folder + "/" + fileName;
        
        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
        
        Blob blob = storage.create(blobInfo, file.getBytes());
        
        log.info("File uploaded successfully: {}", blob.getMediaLink());
        return "gs://" + bucketName + "/" + filePath;
    }

    public byte[] downloadFile(String filePath) {
        log.info("Downloading file: {}", filePath);
        
        String blobName = extractBlobName(filePath);
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);
        
        if (blob == null) {
            throw new RuntimeException("File not found: " + filePath);
        }
        
        return blob.getContent();
    }

    public String generateSignedUrl(String filePath, long expirationTime) {
        log.info("Generating signed URL for file: {}", filePath);
        
        String blobName = extractBlobName(filePath);
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);
        
        if (blob == null) {
            throw new RuntimeException("File not found: " + filePath);
        }
        
        return blob.signUrl(expirationTime, java.util.concurrent.TimeUnit.MILLISECONDS).toString();
    }

    public void deleteFile(String filePath) {
        log.info("Deleting file: {}", filePath);
        
        String blobName = extractBlobName(filePath);
        BlobId blobId = BlobId.of(bucketName, blobName);
        boolean deleted = storage.delete(blobId);
        
        if (!deleted) {
            log.warn("File not found or could not be deleted: {}", filePath);
        } else {
            log.info("File deleted successfully: {}", filePath);
        }
    }

    public boolean fileExists(String filePath) {
        String blobName = extractBlobName(filePath);
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);
        return blob != null && blob.exists();
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String extractBlobName(String filePath) {
        if (filePath.startsWith("gs://")) {
            String[] parts = filePath.split("/", 4);
            if (parts.length >= 4) {
                return parts[3];
            }
        }
        return filePath;
    }
}
