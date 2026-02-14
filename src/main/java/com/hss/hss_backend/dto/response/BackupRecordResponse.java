package com.hss.hss_backend.dto.response;

import com.hss.hss_backend.entity.BackupRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupRecordResponse {

    private Long backupId;
    private BackupRecord.BackupType backupType;
    private String backupName;
    private String filePath;
    private Long fileSize;
    private BackupRecord.Status status;
    private LocalDateTime backupDate;
    private LocalDateTime completedAt;
    private Boolean verified;
    private LocalDateTime verificationDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

