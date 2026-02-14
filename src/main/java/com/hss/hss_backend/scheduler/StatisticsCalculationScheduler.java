package com.hss.hss_backend.scheduler;

import com.hss.hss_backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticsCalculationScheduler {

    private final StatisticsService statisticsService;

    // Her gün saat 02:00'de çalışır
    @Scheduled(cron = "0 0 2 * * ?")
    public void calculateDailyStatistics() {
        log.info("Scheduled job: Calculating daily statistics");
        try {
            statisticsService.calculateAndCacheStatistics();
        } catch (Exception e) {
            log.error("Error in scheduled statistics calculation", e);
        }
    }
}

