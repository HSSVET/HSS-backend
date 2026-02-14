package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.response.BackupRecordResponse;
import com.hss.hss_backend.entity.BackupRecord;
import com.hss.hss_backend.service.BackupService;
import com.hss.hss_backend.service.RestoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Backup", description = "Backup and restore management APIs")
public class BackupController {

    private final BackupService backupService;
    private final RestoreService restoreService;

    @PostMapping("/full")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a full backup (Admin only)")
    public ResponseEntity<BackupRecordResponse> createFullBackup() {
        log.info("Creating full backup");
        BackupRecord backup = backupService.createFullBackup();
        return ResponseEntity.ok(toResponse(backup));
    }

    @PostMapping("/database")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a database backup (Admin only)")
    public ResponseEntity<BackupRecordResponse> createDatabaseBackup() {
        log.info("Creating database backup");
        BackupRecord backup = backupService.createDatabaseBackup();
        return ResponseEntity.ok(toResponse(backup));
    }

    @PostMapping("/files")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a files backup (Admin only)")
    public ResponseEntity<BackupRecordResponse> createFilesBackup() {
        log.info("Creating files backup");
        BackupRecord backup = backupService.createFilesBackup();
        return ResponseEntity.ok(toResponse(backup));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all backups (Admin only)")
    public ResponseEntity<List<BackupRecordResponse>> getAllBackups() {
        log.info("Fetching all backups");
        List<BackupRecord> backups = backupService.getAllBackups();
        return ResponseEntity.ok(backups.stream()
            .map(this::toResponse)
            .collect(Collectors.toList()));
    }

    @GetMapping("/completed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get completed backups (Admin only)")
    public ResponseEntity<List<BackupRecordResponse>> getCompletedBackups() {
        log.info("Fetching completed backups");
        List<BackupRecord> backups = backupService.getCompletedBackups();
        return ResponseEntity.ok(backups.stream()
            .map(this::toResponse)
            .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get backup by ID (Admin only)")
    public ResponseEntity<BackupRecordResponse> getBackupById(@PathVariable Long id) {
        log.info("Fetching backup with ID: {}", id);
        BackupRecord backup = backupService.getBackupById(id);
        return ResponseEntity.ok(toResponse(backup));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a backup (Admin only)")
    public ResponseEntity<Void> deleteBackup(@PathVariable Long id) {
        log.info("Deleting backup with ID: {}", id);
        backupService.deleteBackup(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restore from a backup (Admin only)")
    public ResponseEntity<Void> restoreBackup(@PathVariable Long id) {
        log.info("Restoring backup with ID: {}", id);
        try {
            restoreService.restoreBackup(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Error restoring backup: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private BackupRecordResponse toResponse(BackupRecord backup) {
        return BackupRecordResponse.builder()
            .backupId(backup.getBackupId())
            .backupType(backup.getBackupType())
            .backupName(backup.getBackupName())
            .filePath(backup.getFilePath())
            .fileSize(backup.getFileSize())
            .status(backup.getStatus())
            .backupDate(backup.getBackupDate())
            .completedAt(backup.getCompletedAt())
            .verified(backup.getVerified())
            .verificationDate(backup.getVerificationDate())
            .notes(backup.getNotes())
            .createdAt(backup.getCreatedAt())
            .updatedAt(backup.getUpdatedAt())
            .build();
    }
}

