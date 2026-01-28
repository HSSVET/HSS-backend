package com.hss.hss_backend.scheduler;

import com.hss.hss_backend.entity.StockProduct;
import com.hss.hss_backend.repository.StockProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler component for monitoring stock levels and expiration dates.
 * Generates alerts for low stock and expiring products.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StockAlertScheduler {

    private final StockProductRepository stockProductRepository;

    /**
     * Check for low stock products and log warnings.
     * Runs daily at 07:00.
     */
    @Scheduled(cron = "0 0 7 * * ?")
    @Transactional(readOnly = true)
    public void checkLowStockAlerts() {
        log.info("Starting low stock alert check...");

        List<StockProduct> lowStockProducts = stockProductRepository.findLowStockProducts();

        if (!lowStockProducts.isEmpty()) {
            log.warn("Found {} products with low stock!", lowStockProducts.size());

            for (StockProduct product : lowStockProducts) {
                log.warn("LOW STOCK ALERT: {} - Current: {}, Minimum: {}",
                        product.getName(),
                        product.getCurrentStock(),
                        product.getMinStock());

                // TODO: Send notification to clinic admin (email/push notification)
                // This could be enhanced to send emails or push notifications
            }
        } else {
            log.info("No low stock alerts.");
        }

        log.info("Low stock alert check completed.");
    }

    /**
     * Check for products expiring within the next 30 days.
     * Runs daily at 06:00.
     */
    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional(readOnly = true)
    public void checkExpiringProducts() {
        log.info("Starting expiring products check...");

        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        List<StockProduct> expiringProducts = stockProductRepository.findExpiringProducts(thirtyDaysFromNow);

        if (!expiringProducts.isEmpty()) {
            log.warn("Found {} products expiring within 30 days!", expiringProducts.size());

            for (StockProduct product : expiringProducts) {
                long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(
                        LocalDate.now(), product.getExpirationDate());

                if (daysUntilExpiry <= 7) {
                    log.error("CRITICAL: {} expires in {} days! Lot: {}",
                            product.getName(),
                            daysUntilExpiry,
                            product.getLotNo());
                } else if (daysUntilExpiry <= 14) {
                    log.warn("WARNING: {} expires in {} days! Lot: {}",
                            product.getName(),
                            daysUntilExpiry,
                            product.getLotNo());
                } else {
                    log.info("NOTICE: {} expires in {} days. Lot: {}",
                            product.getName(),
                            daysUntilExpiry,
                            product.getLotNo());
                }
            }
        } else {
            log.info("No products expiring within 30 days.");
        }

        log.info("Expiring products check completed.");
    }

    /**
     * Generate daily stock summary report.
     * Runs daily at 23:00.
     */
    @Scheduled(cron = "0 0 23 * * ?")
    @Transactional(readOnly = true)
    public void generateDailyStockSummary() {
        log.info("Generating daily stock summary...");

        List<StockProduct> allActiveProducts = stockProductRepository.findByIsActive(true);

        long vaccineCount = allActiveProducts.stream()
                .filter(p -> p.getCategory() == StockProduct.Category.VACCINE)
                .count();

        long medicineCount = allActiveProducts.stream()
                .filter(p -> p.getCategory() == StockProduct.Category.MEDICINE)
                .count();

        long lowStockCount = allActiveProducts.stream()
                .filter(p -> p.getCurrentStock() != null && p.getMinStock() != null
                        && p.getCurrentStock() <= p.getMinStock())
                .count();

        long outOfStockCount = allActiveProducts.stream()
                .filter(p -> p.getCurrentStock() != null && p.getCurrentStock() == 0)
                .count();

        log.info("=== Daily Stock Summary ===");
        log.info("Total Active Products: {}", allActiveProducts.size());
        log.info("Vaccines: {}", vaccineCount);
        log.info("Medicines: {}", medicineCount);
        log.info("Low Stock Items: {}", lowStockCount);
        log.info("Out of Stock Items: {}", outOfStockCount);
        log.info("===========================");
    }
}
