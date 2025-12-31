package com.hss.hss_backend.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileBackupService {

    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket-name}")
    private String bucketName;

    @Value("${backup.files.path:/tmp/backups/files}")
    private String filesBackupPath;

    @Value("${backup.files.source:/tmp/uploads}")
    private String filesSourcePath;

    public String createFilesBackup() throws IOException {
        log.info("Creating files backup");
        
        // Backup dizinini oluştur
        Path backupDir = Paths.get(filesBackupPath);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }

        // Backup dosya adı
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFileName = String.format("files_backup_%s.zip", timestamp);
        String backupFilePath = filesBackupPath + "/" + backupFileName;

        // Dosyaları zip'le
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backupFilePath))) {
            File sourceDir = new File(filesSourcePath);
            if (sourceDir.exists() && sourceDir.isDirectory()) {
                zipDirectory(sourceDir, sourceDir.getName(), zos);
            }
        }

        File backupFile = new File(backupFilePath);
        long fileSize = backupFile.length();
        log.info("Files backup created successfully: {} ({} bytes)", backupFilePath, fileSize);
        return backupFilePath;
    }

    private void zipDirectory(File directory, String baseName, ZipOutputStream zos) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(file, baseName + "/" + file.getName(), zos);
            } else {
                ZipEntry zipEntry = new ZipEntry(baseName + "/" + file.getName());
                zos.putNextEntry(zipEntry);
                Files.copy(file.toPath(), zos);
                zos.closeEntry();
            }
        }
    }

    public String uploadBackupToCloudStorage(String localFilePath, String backupType) throws IOException {
        log.info("Uploading backup to Cloud Storage: {}", localFilePath);
        
        File backupFile = new File(localFilePath);
        if (!backupFile.exists()) {
            throw new IOException("Backup file not found: " + localFilePath);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String cloudPath = String.format("backups/%s/%s_%s", backupType, backupType, timestamp);
        
        BlobId blobId = BlobId.of(bucketName, cloudPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType("application/octet-stream")
            .build();

        try (FileInputStream fis = new FileInputStream(backupFile)) {
            Blob blob = storage.create(blobInfo, fis.readAllBytes());
            log.info("Backup uploaded to Cloud Storage: gs://{}/{}", bucketName, cloudPath);
            return "gs://" + bucketName + "/" + cloudPath;
        }
    }

    public void downloadBackupFromCloudStorage(String cloudPath, String localFilePath) throws IOException {
        log.info("Downloading backup from Cloud Storage: {}", cloudPath);
        
        String blobName = cloudPath.replace("gs://" + bucketName + "/", "");
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);
        
        if (blob == null) {
            throw new IOException("Backup not found in Cloud Storage: " + cloudPath);
        }

        Path localPath = Paths.get(localFilePath);
        Files.createDirectories(localPath.getParent());
        Files.write(localPath, blob.getContent());
        
        log.info("Backup downloaded from Cloud Storage to: {}", localFilePath);
    }
}

