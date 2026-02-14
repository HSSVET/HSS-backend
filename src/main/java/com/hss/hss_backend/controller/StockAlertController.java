package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.response.StockAlertResponse;
import com.hss.hss_backend.entity.StockAlert;
import com.hss.hss_backend.service.StockAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-alerts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Stock Alert", description = "Stock alert management APIs")
public class StockAlertController {

    private final StockAlertService stockAlertService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get all active stock alerts")
    public ResponseEntity<List<StockAlertResponse>> getActiveAlerts() {
        log.info("Fetching all active stock alerts");
        List<StockAlertResponse> alerts = stockAlertService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/type/{alertType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get alerts by type")
    public ResponseEntity<List<StockAlertResponse>> getAlertsByType(@PathVariable StockAlert.AlertType alertType) {
        log.info("Fetching stock alerts by type: {}", alertType);
        List<StockAlertResponse> alerts = stockAlertService.getAlertsByType(alertType);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get alerts by product ID")
    public ResponseEntity<List<StockAlertResponse>> getAlertsByProductId(@PathVariable Long productId) {
        log.info("Fetching stock alerts for product ID: {}", productId);
        List<StockAlertResponse> alerts = stockAlertService.getAlertsByProductId(productId);
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Resolve a stock alert")
    public ResponseEntity<Void> resolveAlert(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "system") String resolvedBy) {
        log.info("Resolving alert ID: {} by {}", id, resolvedBy);
        stockAlertService.resolveAlert(id, resolvedBy);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/product/{productId}/resolve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Resolve all alerts for a product")
    public ResponseEntity<Void> resolveAlertsByProductId(
            @PathVariable Long productId,
            @RequestParam(required = false, defaultValue = "system") String resolvedBy) {
        log.info("Resolving all alerts for product ID: {} by {}", productId, resolvedBy);
        stockAlertService.resolveAlertsByProductId(productId, resolvedBy);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Manually trigger stock alert check (Admin only)")
    public ResponseEntity<Void> checkStockAlerts() {
        log.info("Manually triggering stock alert check");
        stockAlertService.checkAndCreateAlerts();
        return ResponseEntity.ok().build();
    }
}

