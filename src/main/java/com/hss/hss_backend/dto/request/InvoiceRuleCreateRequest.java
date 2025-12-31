package com.hss.hss_backend.dto.request;

import com.hss.hss_backend.entity.InvoiceRule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvoiceRuleCreateRequest {

    @NotBlank(message = "Rule name is required")
    private String ruleName;

    @NotNull(message = "Rule type is required")
    private InvoiceRule.RuleType ruleType;

    private String triggerEntity;
    private String triggerStatus;
    private String conditions; // JSON string
    private String invoiceTemplate; // JSON string
    private Integer dueDays;
    private Boolean isActive;
    private Integer priority;
    private String description;
    private String notes;
}

