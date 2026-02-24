package com.hss.hss_backend.scheduler;

import com.hss.hss_backend.service.ReportScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportGenerationScheduler {

    private final ReportScheduleService scheduleService;

    // Her saat başı çalışır
    @Scheduled(cron = "0 0 * * * ?")
    public void processScheduledReports() {
        log.info("Scheduled job: Processing scheduled reports");
        try {
            scheduleService.processScheduledReports();
        } catch (Exception e) {
            log.error("Error in scheduled report generation", e);
        }
    }
}

