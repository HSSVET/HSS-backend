package com.hss.hss_backend.scheduler;

import com.hss.hss_backend.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockAlertScheduler {

    private final StockAlertService stockAlertService;

    // Her saat başı çalışır
    @Scheduled(cron = "0 0 * * * ?")
    public void checkStockLevels() {
        log.info("Scheduled job: Checking stock levels and creating alerts");
        try {
            stockAlertService.checkAndCreateAlerts();
        } catch (Exception e) {
            log.error("Error in scheduled stock alert check", e);
        }
    }

    // Her gün saat 08:00'de çalışır
    @Scheduled(cron = "0 0 8 * * ?")
    public void dailyStockCheck() {
        log.info("Scheduled job: Daily stock level check");
        try {
            stockAlertService.checkAndCreateAlerts();
        } catch (Exception e) {
            log.error("Error in daily stock alert check", e);
        }
    }
}

