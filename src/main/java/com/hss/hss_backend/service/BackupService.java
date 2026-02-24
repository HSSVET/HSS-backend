package com.hss.hss_backend.service;

import com.hss.hss_backend.entity.BackupRecord;
import com.hss.hss_backend.repository.BackupRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BackupService {

    private final BackupRecordRepository backupRecordRepository;
    private final DatabaseBackupService databaseBackupService;
    private final FileBackupService fileBackupService;

    @Value("${backup.upload.to.cloud:true}")
    private boolean uploadToCloud;

    public BackupRecord createFullBackup() {
        log.info("Creating full backup");
        return createBackup(BackupRecord.BackupType.FULL);
    }

    public BackupRecord createDatabaseBackup() {
        log.info("Creating database backup");
        return createBackup(BackupRecord.BackupType.DATABASE);
    }

    public BackupRecord createFilesBackup() {
        log.info("Creating files backup");
        return createBackup(BackupRecord.BackupType.FILES);
    }

    private BackupRecord createBackup(BackupRecord.BackupType backupType) {
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupName = String.format("%s_backup_%s", backupType.name().toLowerCase(), timestamp);

        BackupRecord backupRecord = BackupRecord.builder()
                .backupType(backupType)
                .backupName(backupName)
                .status(BackupRecord.Status.IN_PROGRESS)
                .backupDate(LocalDateTime.now())
                .verified(false)
                .build();

        backupRecord = backupRecordRepository.save(backupRecord);

        try {
            String filePath = null;
            long fileSize = 0;

            if (backupType == BackupRecord.BackupType.FULL || backupType == BackupRecord.BackupType.DATABASE) {
                filePath = databaseBackupService.createDatabaseBackup();
                File file = new File(filePath);
                fileSize = file.length();
            }

            if (backupType == BackupRecord.BackupType.FULL || backupType == BackupRecord.BackupType.FILES) {
                String filesPath = fileBackupService.createFilesBackup();
                File filesFile = new File(filesPath);
                fileSize += filesFile.length();
                if (filePath == null) {
                    filePath = filesPath;
                }
            }

            // Cloud Storage'a yükle
            String cloudPath = null;
            if (uploadToCloud && filePath != null) {
                cloudPath = fileBackupService.uploadBackupToCloudStorage(filePath, backupType.name());
            }

            backupRecord.setFilePath(cloudPath != null ? cloudPath : filePath);
            backupRecord.setFileSize(fileSize);
            backupRecord.setStatus(BackupRecord.Status.COMPLETED);
            backupRecord.setCompletedAt(LocalDateTime.now());
            backupRecord.setVerified(true);
            backupRecord.setVerificationDate(LocalDateTime.now());

            backupRecordRepository.save(backupRecord);
            log.info("Backup created successfully: {}", backupName);
        } catch (Exception e) {
            log.error("Error creating backup: {}", e.getMessage(), e);
            backupRecord.setStatus(BackupRecord.Status.FAILED);
            backupRecord.setNotes("Error: " + e.getMessage());
            backupRecordRepository.save(backupRecord);
        }

        return backupRecord;
    }

    @Transactional(readOnly = true)
    public List<BackupRecord> getAllBackups() {
        log.info("Fetching all backups");
        return backupRecordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BackupRecord> getCompletedBackups() {
        log.info("Fetching completed backups");
        return backupRecordRepository.findCompletedBackups();
    }

    @Transactional(readOnly = true)
    public BackupRecord getBackupById(Long id) {
        log.info("Fetching backup with ID: {}", id);
        return backupRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Backup not found: " + id));
    }

    public void deleteBackup(Long id) {
        log.info("Deleting backup with ID: {}", id);
        BackupRecord backup = backupRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Backup not found: " + id));

        // Dosyayı sil
        if (backup.getFilePath() != null) {
            try {
                if (backup.getFilePath().startsWith("gs://")) {
                    // Cloud Storage'dan sil
                    log.info("Deleting backup from Cloud Storage: {}", backup.getFilePath());
                    // TODO: Cloud Storage delete implementasyonu
                } else {
                    // Local dosyayı sil
                    File file = new File(backup.getFilePath());
                    if (file.exists()) {
                        file.delete();
                    }
                }
            } catch (Exception e) {
                log.error("Error deleting backup file: {}", e.getMessage());
            }
        }

        backup.setStatus(BackupRecord.Status.DELETED);
        backupRecordRepository.save(backup);
        log.info("Backup deleted successfully");
    }
}

