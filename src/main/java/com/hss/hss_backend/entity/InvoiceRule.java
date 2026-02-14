package com.hss.hss_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice_rule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InvoiceRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false, length = 50)
    private RuleType ruleType;

    @Column(name = "trigger_entity", length = 50)
    private String triggerEntity;

    @Column(name = "trigger_status", length = 50)
    private String triggerStatus;

    @Column(name = "conditions", columnDefinition = "jsonb")
    private String conditions; // JSON string for rule conditions

    @Column(name = "invoice_template", columnDefinition = "jsonb")
    private String invoiceTemplate; // JSON string for invoice items template

    @Column(name = "due_days")
    @Builder.Default
    private Integer dueDays = 30;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum RuleType {
        APPOINTMENT_AFTER,      // Randevu sonrası
        MONTHLY_SUBSCRIPTION,   // Aylık abonelik
        TREATMENT_AFTER,        // Tedavi sonrası
        VACCINATION_AFTER,      // Aşı sonrası
        LAB_TEST_AFTER,         // Lab test sonrası
        CUSTOM                  // Özel kural
    }
}

