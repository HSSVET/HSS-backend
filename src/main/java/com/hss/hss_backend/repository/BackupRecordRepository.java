package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.BackupRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BackupRecordRepository extends JpaRepository<BackupRecord, Long> {

    List<BackupRecord> findByBackupType(BackupRecord.BackupType backupType);

    List<BackupRecord> findByStatus(BackupRecord.Status status);

    @Query("SELECT br FROM BackupRecord br WHERE br.status = 'COMPLETED' ORDER BY br.backupDate DESC")
    List<BackupRecord> findCompletedBackups();

    @Query("SELECT br FROM BackupRecord br WHERE br.backupDate BETWEEN :startDate AND :endDate")
    List<BackupRecord> findBackupsBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT br FROM BackupRecord br WHERE br.backupType = :type AND br.status = 'COMPLETED' ORDER BY br.backupDate DESC")
    List<BackupRecord> findCompletedBackupsByType(@Param("type") BackupRecord.BackupType type);

    Optional<BackupRecord> findByBackupName(String backupName);
}

