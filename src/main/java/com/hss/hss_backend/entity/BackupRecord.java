package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "backup_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BackupRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "backup_id")
    private Long backupId;

    @Enumerated(EnumType.STRING)
    @Column(name = "backup_type", nullable = false, length = 20)
    private BackupType backupType;

    @Column(name = "backup_name", nullable = false, length = 200)
    private String backupName;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private Status status = Status.IN_PROGRESS;

    @Column(name = "backup_date", nullable = false)
    @Builder.Default
    private LocalDateTime backupDate = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "verified")
    @Builder.Default
    private Boolean verified = false;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum BackupType {
        FULL,           // Tam backup (veritabanı + dosyalar)
        INCREMENTAL,    // Artımlı backup
        DATABASE,       // Sadece veritabanı
        FILES           // Sadece dosyalar
    }

    public enum Status {
        IN_PROGRESS, COMPLETED, FAILED, DELETED
    }
}

