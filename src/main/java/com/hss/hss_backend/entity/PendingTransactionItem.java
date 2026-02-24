package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a single item (service) in a pending transaction.
 * Links to the actual service entity (vaccination, surgery, lab test, etc.)
 */
@Entity
@Table(name = "pending_transaction_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PendingTransactionItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pending_transaction_item_id")
    private Long pendingTransactionItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pending_transaction_id", nullable = false)
    private PendingTransaction pendingTransaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 50)
    private ServiceType serviceType;

    @Column(name = "service_id")
    private Long serviceId; // ID of the related entity (VaccinationRecord, Surgery, LabTest, etc.)

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "quantity")
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vet_service_id")
    private VetService vetService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_product_id")
    private StockProduct stockProduct;

    public enum ServiceType {
        EXAMINATION,      // General examination
        VACCINATION,      // Vaccination service
        SURGERY,          // Surgical procedure
        LAB_TEST,         // Laboratory test
        RADIOLOGY,        // X-ray, ultrasound, etc.
        HOSPITALIZATION,  // Hospital stay per day
        MEDICATION,       // Medicine from stock
        GROOMING,         // Grooming service
        CONSULTATION,     // Consultation fee
        EMERGENCY,        // Emergency fee
        FOLLOW_UP,        // Follow-up visit
        OTHER             // Other services
    }

    @PrePersist
    @PreUpdate
    protected void calculateLineTotal() {
        if (unitPrice != null && quantity != null) {
            BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
            if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
                total = total.subtract(discount);
            }
            this.lineTotal = total.max(BigDecimal.ZERO);
        }
    }
}
