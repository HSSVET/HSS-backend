package com.hss.hss_backend.scheduler;

import com.hss.hss_backend.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final ReminderService reminderService;

    // Her 5 dakikada bir çalışır
    @Scheduled(fixedRate = 300000) // 5 dakika = 300000 ms
    public void processPendingReminders() {
        log.info("Scheduled job: Processing pending reminders");
        try {
            reminderService.processPendingReminders();
        } catch (Exception e) {
            log.error("Error in scheduled reminder processing", e);
        }
    }

    // Her saat başı çalışır
    @Scheduled(cron = "0 0 * * * ?")
    public void retryFailedReminders() {
        log.info("Scheduled job: Retrying failed reminders");
        try {
            reminderService.retryFailedReminders();
        } catch (Exception e) {
            log.error("Error in scheduled reminder retry", e);
        }
    }
}

