package com.hss.hss_backend.dto.response;

import com.hss.hss_backend.entity.InvoiceRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRuleResponse {

    private Long ruleId;
    private String ruleName;
    private InvoiceRule.RuleType ruleType;
    private String triggerEntity;
    private String triggerStatus;
    private String conditions;
    private String invoiceTemplate;
    private Integer dueDays;
    private Boolean isActive;
    private Integer priority;
    private String description;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

