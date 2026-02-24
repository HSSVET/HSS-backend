package com.hss.hss_backend.dto.response;

import com.hss.hss_backend.entity.StockAlert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertResponse {

    private Long alertId;
    private Long productId;
    private String productName;
    private String productBarcode;
    private StockAlert.AlertType alertType;
    private Integer currentStock;
    private Integer thresholdValue;
    private Integer minStock;
    private Integer maxStock;
    private LocalDate expirationDate;
    private String message;
    private Boolean isResolved;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

