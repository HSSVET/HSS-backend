package com.hss.hss_backend.scheduler;

import com.hss.hss_backend.service.InvoiceRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceGenerationScheduler {

    private final InvoiceRuleService invoiceRuleService;

    // Her gün saat 09:00'de çalışır
    @Scheduled(cron = "0 0 9 * * ?")
    public void processInvoiceRules() {
        log.info("Scheduled job: Processing invoice rules");
        try {
            invoiceRuleService.processAllRules();
        } catch (Exception e) {
            log.error("Error in scheduled invoice rule processing", e);
        }
    }
}

