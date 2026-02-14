package com.hss.hss_backend.scheduler;

import com.hss.hss_backend.service.VaccinationScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VaccinationScheduleScheduler {

    private final VaccinationScheduleService scheduleService;

    // Her gün saat 08:00'de çalışır
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkOverdueSchedules() {
        log.info("Scheduled job: Checking for overdue vaccination schedules");
        try {
            scheduleService.checkAndUpdateOverdueSchedules();
        } catch (Exception e) {
            log.error("Error in scheduled vaccination schedule check", e);
        }
    }
}

