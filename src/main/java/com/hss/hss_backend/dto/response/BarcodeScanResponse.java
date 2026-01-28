package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO containing stock product information from barcode scan.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarcodeScanResponse {

    private Long productId;
    private String name;
    private String barcode;
    private String lotNo;
    private String serialNumber;
    private LocalDate productionDate;
    private LocalDate expirationDate;
    private Integer currentStock;
    private BigDecimal unitCost;
    private BigDecimal sellingPrice;
    private String category;
    private String supplier;
    private String location;
    private Boolean isActive;

    // Validation flags
    private Boolean isValid;
    private Boolean isExpired;
    private Boolean isLowStock;
    private String warningMessage;

    public static BarcodeScanResponse notFound(String barcode) {
        return BarcodeScanResponse.builder()
                .barcode(barcode)
                .isValid(false)
                .warningMessage("Barkod bulunamadı: " + barcode)
                .build();
    }

    public static BarcodeScanResponse expired(Long productId, String name, String barcode) {
        return BarcodeScanResponse.builder()
                .productId(productId)
                .name(name)
                .barcode(barcode)
                .isValid(false)
                .isExpired(true)
                .warningMessage("Ürün son kullanma tarihi geçmiş!")
                .build();
    }

    public static BarcodeScanResponse outOfStock(Long productId, String name, String barcode) {
        return BarcodeScanResponse.builder()
                .productId(productId)
                .name(name)
                .barcode(barcode)
                .isValid(false)
                .currentStock(0)
                .warningMessage("Ürün stokta bulunmuyor!")
                .build();
    }
}
