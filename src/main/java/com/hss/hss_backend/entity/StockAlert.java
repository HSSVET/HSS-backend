package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_alert")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StockAlert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long alertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private StockProduct stockProduct;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 20)
    private AlertType alertType;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;

    @Column(name = "threshold_value")
    private Integer thresholdValue;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by", length = 100)
    private String resolvedBy;

    public enum AlertType {
        LOW_STOCK,          // Stok minimum seviyenin altında
        CRITICAL_STOCK,     // Stok kritik seviyede
        OUT_OF_STOCK,       // Stok tükendi
        EXPIRING_SOON,      // Son kullanma tarihi yaklaşıyor
        EXPIRED             // Son kullanma tarihi geçti
    }
}

