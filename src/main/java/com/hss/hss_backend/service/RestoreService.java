package com.hss.hss_backend.service;

import com.hss.hss_backend.entity.BackupRecord;
import com.hss.hss_backend.repository.BackupRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RestoreService {

    private final BackupRecordRepository backupRecordRepository;
    private final DatabaseBackupService databaseBackupService;
    private final FileBackupService fileBackupService;

    public void restoreBackup(Long backupId) throws IOException {
        log.info("Restoring backup with ID: {}", backupId);
        
        BackupRecord backup = backupRecordRepository.findById(backupId)
                .orElseThrow(() -> new RuntimeException("Backup not found: " + backupId));

        if (backup.getStatus() != BackupRecord.Status.COMPLETED) {
            throw new IllegalStateException("Cannot restore backup with status: " + backup.getStatus());
        }

        if (backup.getFilePath() == null) {
            throw new IllegalStateException("Backup file path is null");
        }

        try {
            String localFilePath = backup.getFilePath();
            
            // Eğer Cloud Storage'daysa, önce indir
            if (backup.getFilePath().startsWith("gs://")) {
                String tempPath = "/tmp/restore_" + backup.getBackupId() + ".sql";
                fileBackupService.downloadBackupFromCloudStorage(backup.getFilePath(), tempPath);
                localFilePath = tempPath;
            }

            // Backup tipine göre restore et
            if (backup.getBackupType() == BackupRecord.BackupType.FULL || 
                backup.getBackupType() == BackupRecord.BackupType.DATABASE) {
                databaseBackupService.restoreDatabaseBackup(localFilePath);
            }

            // TODO: Files restore implementasyonu

            log.info("Backup restored successfully: {}", backup.getBackupName());
        } catch (Exception e) {
            log.error("Error restoring backup: {}", e.getMessage(), e);
            throw new IOException("Failed to restore backup: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public BackupRecord getBackupForRestore(Long id) {
        log.info("Fetching backup for restore with ID: {}", id);
        BackupRecord backup = backupRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Backup not found: " + id));

        if (backup.getStatus() != BackupRecord.Status.COMPLETED) {
            throw new IllegalStateException("Cannot restore backup with status: " + backup.getStatus());
        }

        return backup;
    }
}

