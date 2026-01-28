package com.hss.hss_backend.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    @Value("${spring.cloud.gcp.storage.bucket-name:hss-storage-bucket}")
    private String bucketName;

    @Value("${storage.local.enabled:true}")
    private boolean useLocalStorageFallback;

    // Use @Autowired(required = false) via constructor or just handle potential
    // nulls if we remove final
    // But since we use @RequiredArgsConstructor, we assume it's injected.
    // If we want to make it optional, we need to change how it's injected.
    // For now, let's assume it IS injected but might throw errors on use.
    // Optional dependency for GCP Storage
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private Storage storage;

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        log.info("Uploading file: {} to folder: {}", file.getOriginalFilename(), folder);

        if (storage != null) {
            try {
                return uploadToGcp(file, folder);
            } catch (Exception e) {
                log.warn("GCP upload failed. Error: {}", e.getMessage());
                if (!useLocalStorageFallback) {
                    throw new IOException("GCP upload failed and local fallback is disabled.", e);
                }
                log.info("Falling back to local storage.");
            }
        } else {
            log.warn("GCP Storage bean is null.");
            if (!useLocalStorageFallback) {
                throw new IOException("GCP Storage not configured and local fallback is disabled.");
            }
        }

        return uploadToLocal(file, folder);
    }

    private String uploadToGcp(MultipartFile file, String folder) throws IOException {
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String filePath = folder + "/" + fileName;

        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        Blob blob = storage.create(blobInfo, file.getBytes());

        log.info("File uploaded successfully to GCP: {}", blob.getMediaLink());
        return "gs://" + bucketName + "/" + filePath;
    }

    private String uploadToLocal(MultipartFile file, String folder) throws IOException {
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        // Create local uploads directory
        java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads", folder);
        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }

        java.nio.file.Path filePath = uploadPath.resolve(fileName);
        java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = "/uploads/" + folder + "/" + fileName;
        log.info("File uploaded successfully to local: {}", fileUrl);
        return fileUrl;
    }

    public byte[] downloadFile(String filePath) {
        // Simple implementation that supports both GS and local
        if (filePath.startsWith("gs://")) {
            return downloadFromGcp(filePath);
        } else if (filePath.startsWith("/uploads/") && useLocalStorageFallback) {
            return downloadFromLocal(filePath);
        } else if (filePath.startsWith("/uploads/") && !useLocalStorageFallback) {
            // Treat as relative path for GCS
            return downloadFromGcp(filePath);
        }
        // Default relative path behavior (usually GCS if not strictly local)
        return downloadFromGcp(filePath);
    }

    private byte[] downloadFromGcp(String filePath) {
        log.info("Downloading file from GCP: {}", filePath);
        BlobId blobId = getBlobId(filePath);
        Blob blob = getBlobWithFallback(blobId);

        if (blob == null) {
            throw new RuntimeException("File not found in GCP: " + filePath);
        }
        return blob.getContent();
    }

    private byte[] downloadFromLocal(String filePath) {
        try {
            String localPathStr = filePath.startsWith("/") ? filePath.substring(1) : filePath;
            // Handle if filePath contains "uploads" prefix but not starting with /
            if (!localPathStr.startsWith("uploads/")) {
                // Assuming filePath is relative to project root or uploads folder?
                // Logic in uploadToLocal places it in "uploads/" + folder
                // filePath usually comes as "/uploads/folder/file"
            }
            java.nio.file.Path path = java.nio.file.Paths.get(localPathStr);
            return java.nio.file.Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read local file: " + filePath, e);
        }
    }

    public String generateSignedUrl(String filePath, long expirationTime) {
        // Local files don't need signed URLs, but since we don't serve static files
        // directly,
        // we redirect to our download endpoint which handles local file reading.
        // We append a query param to indicate we want inline viewing.
        if (filePath.startsWith("/uploads/") && useLocalStorageFallback) {
            // Encode the file path to ensure safety in URL
            String encodedPath = java.net.URLEncoder.encode(filePath, java.nio.charset.StandardCharsets.UTF_8);
            return "http://localhost:8090/api/files/download?filePath=" + encodedPath + "&inline=true";
        }

        log.info("Generating signed URL for file: {}", filePath);

        BlobId blobId = getBlobId(filePath);
        Blob blob = getBlobWithFallback(blobId);

        if (blob == null) {
            // Throwing exception here might break valid local file requests if logic is
            // mixed,
            // but for GCS paths it's correct.
            throw new RuntimeException("File not found in GCP: " + filePath + ", bucket: " + blobId.getBucket());
        }

        // Force Content-Disposition: inline to prevent auto-download
        return blob.signUrl(expirationTime, java.util.concurrent.TimeUnit.MILLISECONDS,
                Storage.SignUrlOption.withQueryParams(java.util.Map.of("response-content-disposition", "inline")))
                .toString();
    }

    public void deleteFile(String filePath) {
        if (filePath.startsWith("gs://")) {
            BlobId blobId = getBlobId(filePath);
            // Try to find it first to delete correct one, or just try delete
            // For safety/fallback, let's use fallback logic to find it
            Blob blob = getBlobWithFallback(blobId);
            if (blob != null) {
                storage.delete(blob.getBlobId());
            } else {
                // Try deleting original ID just in case it exists but get failed?
                // Or simply treat as not found.
                storage.delete(blobId);
            }
        } else if (filePath.startsWith("/uploads/") && useLocalStorageFallback) {
            // Local delete
            try {
                String localPathStr = filePath.startsWith("/") ? filePath.substring(1) : filePath;
                java.nio.file.Path path = java.nio.file.Paths.get(localPathStr);
                java.nio.file.Files.deleteIfExists(path);
            } catch (IOException e) {
                log.warn("Failed to delete local file: {}", filePath);
            }
        } else {
            // GCS delete for relative path
            BlobId blobId = getBlobId(filePath);
            storage.delete(blobId);
        }
    }

    public boolean fileExists(String filePath) {
        if (filePath.startsWith("gs://")) {
            BlobId blobId = getBlobId(filePath);
            Blob blob = getBlobWithFallback(blobId);
            return blob != null && blob.exists();
        } else if (filePath.startsWith("/uploads/") && useLocalStorageFallback) {
            String localPathStr = filePath.startsWith("/") ? filePath.substring(1) : filePath;
            return java.nio.file.Files.exists(java.nio.file.Paths.get(localPathStr));
        } else {
            // Check GCS for relative path
            BlobId blobId = getBlobId(filePath);
            Blob blob = storage.get(blobId);
            return blob != null && blob.exists();
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * Extracts BlobId from a file path.
     * Supports both "gs://bucket/blob" and "blob" formats.
     * If gs:// format is used, it respects the bucket in the path.
     * If relative path is used, it uses the configured default bucket.
     */
    private BlobId getBlobId(String filePath) {
        if (filePath.startsWith("gs://")) {
            // Format: gs://bucketName/blobName
            String pathWithoutProtocol = filePath.substring(5);
            int firstSlashIndex = pathWithoutProtocol.indexOf('/');

            if (firstSlashIndex > 0) {
                String bucketFromPath = pathWithoutProtocol.substring(0, firstSlashIndex);
                String blobName = pathWithoutProtocol.substring(firstSlashIndex + 1);
                return BlobId.of(bucketFromPath, blobName);
            } else {
                // Invalid gs:// path format, fallback or throw
                log.warn("Invalid gs:// path format: {}, falling back to default bucket logic", filePath);
                return BlobId.of(bucketName, pathWithoutProtocol);
            }
        }

        // Relative path, use default bucket and clean path
        String cleanedPath = filePath;
        if (cleanedPath.startsWith("/")) {
            cleanedPath = cleanedPath.substring(1);
        }

        // Legacy handling: if path mistakenly starts with default bucket name but no
        // gs://
        if (cleanedPath.startsWith(bucketName + "/")) {
            cleanedPath = cleanedPath.substring(bucketName.length() + 1);
        }

        return BlobId.of(bucketName, cleanedPath);
    }

    private String extractBlobName(String filePath) {
        // Deprecated/Helper for legacy use cases if any,
        // essentially extracting the name part of the BlobId
        return getBlobId(filePath).getName();
    }

    private Blob getBlobWithFallback(BlobId blobId) {
        Blob blob = storage.get(blobId);
        if (blob == null && !blobId.getBucket().equals(bucketName)) {
            log.info("Detail not found in bucket {}, checking default bucket {}", blobId.getBucket(), bucketName);
            BlobId fallbackBlobId = BlobId.of(bucketName, blobId.getName());
            return storage.get(fallbackBlobId);
        }
        return blob;
    }
}
