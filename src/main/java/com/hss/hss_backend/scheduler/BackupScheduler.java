package com.hss.hss_backend.scheduler;

import com.hss.hss_backend.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BackupScheduler {

    private final BackupService backupService;

    // Her gün saat 03:00'de çalışır
    @Scheduled(cron = "0 0 3 * * ?")
    public void dailyBackup() {
        log.info("Scheduled job: Creating daily database backup");
        try {
            backupService.createDatabaseBackup();
        } catch (Exception e) {
            log.error("Error in scheduled daily backup", e);
        }
    }

    // Her Pazar günü saat 02:00'de çalışır
    @Scheduled(cron = "0 0 2 * * 0")
    public void weeklyFullBackup() {
        log.info("Scheduled job: Creating weekly full backup");
        try {
            backupService.createFullBackup();
        } catch (Exception e) {
            log.error("Error in scheduled weekly backup", e);
        }
    }
}

