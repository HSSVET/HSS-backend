package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.response.StockAlertResponse;
import com.hss.hss_backend.entity.StockAlert;
import com.hss.hss_backend.entity.StockProduct;
import com.hss.hss_backend.repository.StockAlertRepository;
import com.hss.hss_backend.repository.StockProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockAlertService {

    private final StockAlertRepository stockAlertRepository;
    private final StockProductRepository stockProductRepository;
    private final NotificationService notificationService;

    @Value("${stock.alert.critical.threshold:0.2}")
    private double criticalThreshold; // min_stock'un %20'si kritik seviye

    @Value("${stock.alert.expiring.days:30}")
    private int expiringDays; // 30 gün içinde sona erecek ürünler için uyarı

    public void checkAndCreateAlerts() {
        log.info("Checking stock levels and creating alerts");
        
        List<StockProduct> activeProducts = stockProductRepository.findByIsActive(true);
        List<StockAlert> newAlerts = new ArrayList<>();

        for (StockProduct product : activeProducts) {
            // Mevcut aktif alertleri kontrol et
            List<StockAlert> existingAlerts = stockAlertRepository.findActiveAlertsByProductId(product.getProductId());

            // Stok seviyesi kontrolü
            checkStockLevel(product, existingAlerts, newAlerts);

            // Son kullanma tarihi kontrolü
            checkExpirationDate(product, existingAlerts, newAlerts);
        }

        // Yeni alertleri kaydet
        if (!newAlerts.isEmpty()) {
            stockAlertRepository.saveAll(newAlerts);
            log.info("Created {} new stock alerts", newAlerts.size());
            
            // Bildirim gönder
            sendAlertNotifications(newAlerts);
        }
    }

    private void checkStockLevel(StockProduct product, List<StockAlert> existingAlerts, List<StockAlert> newAlerts) {
        int currentStock = product.getCurrentStock();
        int minStock = product.getMinStock() != null ? product.getMinStock() : 0;
        int criticalStock = (int) (minStock * criticalThreshold);

        // OUT_OF_STOCK kontrolü
        if (currentStock == 0) {
            if (!hasActiveAlertOfType(existingAlerts, StockAlert.AlertType.OUT_OF_STOCK)) {
                StockAlert alert = createStockAlert(product, StockAlert.AlertType.OUT_OF_STOCK, 
                    currentStock, minStock, "Ürün stokta tükendi!");
                newAlerts.add(alert);
            }
        }
        // CRITICAL_STOCK kontrolü
        else if (currentStock <= criticalStock && currentStock > 0) {
            if (!hasActiveAlertOfType(existingAlerts, StockAlert.AlertType.CRITICAL_STOCK)) {
                StockAlert alert = createStockAlert(product, StockAlert.AlertType.CRITICAL_STOCK, 
                    currentStock, criticalStock, 
                    String.format("Ürün kritik seviyede! Mevcut stok: %d, Minimum: %d", currentStock, minStock));
                newAlerts.add(alert);
            }
        }
        // LOW_STOCK kontrolü
        else if (currentStock <= minStock && currentStock > criticalStock) {
            if (!hasActiveAlertOfType(existingAlerts, StockAlert.AlertType.LOW_STOCK)) {
                StockAlert alert = createStockAlert(product, StockAlert.AlertType.LOW_STOCK, 
                    currentStock, minStock, 
                    String.format("Ürün stok seviyesi düşük! Mevcut stok: %d, Minimum: %d", currentStock, minStock));
                newAlerts.add(alert);
            }
        }
    }

    private void checkExpirationDate(StockProduct product, List<StockAlert> existingAlerts, List<StockAlert> newAlerts) {
        if (product.getExpirationDate() == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate expirationDate = product.getExpirationDate();
        long daysUntilExpiration = java.time.temporal.ChronoUnit.DAYS.between(today, expirationDate);

        // EXPIRED kontrolü
        if (expirationDate.isBefore(today)) {
            if (!hasActiveAlertOfType(existingAlerts, StockAlert.AlertType.EXPIRED)) {
                StockAlert alert = createExpirationAlert(product, StockAlert.AlertType.EXPIRED, 
                    expirationDate, "Ürün son kullanma tarihi geçmiş!");
                newAlerts.add(alert);
            }
        }
        // EXPIRING_SOON kontrolü
        else if (daysUntilExpiration <= expiringDays && daysUntilExpiration > 0) {
            if (!hasActiveAlertOfType(existingAlerts, StockAlert.AlertType.EXPIRING_SOON)) {
                StockAlert alert = createExpirationAlert(product, StockAlert.AlertType.EXPIRING_SOON, 
                    expirationDate, 
                    String.format("Ürün %d gün içinde sona erecek!", daysUntilExpiration));
                newAlerts.add(alert);
            }
        }
    }

    private boolean hasActiveAlertOfType(List<StockAlert> alerts, StockAlert.AlertType type) {
        return alerts.stream()
                .anyMatch(alert -> alert.getAlertType() == type && !alert.getIsResolved());
    }

    private StockAlert createStockAlert(StockProduct product, StockAlert.AlertType type, 
                                       int currentStock, int threshold, String message) {
        return StockAlert.builder()
                .stockProduct(product)
                .alertType(type)
                .currentStock(currentStock)
                .thresholdValue(threshold)
                .message(message)
                .isResolved(false)
                .build();
    }

    private StockAlert createExpirationAlert(StockProduct product, StockAlert.AlertType type, 
                                            LocalDate expirationDate, String message) {
        return StockAlert.builder()
                .stockProduct(product)
                .alertType(type)
                .currentStock(product.getCurrentStock())
                .expirationDate(expirationDate)
                .message(message)
                .isResolved(false)
                .build();
    }

    private void sendAlertNotifications(List<StockAlert> alerts) {
        // TODO: Admin'lere email/SMS gönder
        log.info("Sending notifications for {} stock alerts", alerts.size());
        // NotificationService kullanarak admin'lere bildirim gönderilebilir
    }

    @Transactional(readOnly = true)
    public List<StockAlertResponse> getActiveAlerts() {
        log.info("Fetching active stock alerts");
        List<StockAlert> alerts = stockAlertRepository.findActiveAlerts();
        return alerts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockAlertResponse> getAlertsByType(StockAlert.AlertType alertType) {
        log.info("Fetching stock alerts by type: {}", alertType);
        List<StockAlert> alerts = stockAlertRepository.findActiveAlertsByType(alertType);
        return alerts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockAlertResponse> getAlertsByProductId(Long productId) {
        log.info("Fetching stock alerts for product ID: {}", productId);
        List<StockAlert> alerts = stockAlertRepository.findActiveAlertsByProductId(productId);
        return alerts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void resolveAlert(Long alertId, String resolvedBy) {
        log.info("Resolving alert ID: {} by {}", alertId, resolvedBy);
        StockAlert alert = stockAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));

        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(resolvedBy);
        stockAlertRepository.save(alert);
        log.info("Alert {} resolved successfully", alertId);
    }

    public void resolveAlertsByProductId(Long productId, String resolvedBy) {
        log.info("Resolving all alerts for product ID: {} by {}", productId, resolvedBy);
        List<StockAlert> alerts = stockAlertRepository.findActiveAlertsByProductId(productId);
        
        LocalDateTime now = LocalDateTime.now();
        for (StockAlert alert : alerts) {
            alert.setIsResolved(true);
            alert.setResolvedAt(now);
            alert.setResolvedBy(resolvedBy);
        }
        
        stockAlertRepository.saveAll(alerts);
        log.info("Resolved {} alerts for product {}", alerts.size(), productId);
    }

    private StockAlertResponse toResponse(StockAlert alert) {
        StockProduct product = alert.getStockProduct();
        return StockAlertResponse.builder()
                .alertId(alert.getAlertId())
                .productId(product.getProductId())
                .productName(product.getName())
                .productBarcode(product.getBarcode())
                .alertType(alert.getAlertType())
                .currentStock(alert.getCurrentStock())
                .thresholdValue(alert.getThresholdValue())
                .minStock(product.getMinStock())
                .maxStock(product.getMaxStock())
                .expirationDate(alert.getExpirationDate())
                .message(alert.getMessage())
                .isResolved(alert.getIsResolved())
                .resolvedAt(alert.getResolvedAt())
                .resolvedBy(alert.getResolvedBy())
                .createdAt(alert.getCreatedAt())
                .updatedAt(alert.getUpdatedAt())
                .build();
    }
}

