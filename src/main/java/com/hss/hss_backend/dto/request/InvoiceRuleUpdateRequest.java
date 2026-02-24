package com.hss.hss_backend.dto.request;

import com.hss.hss_backend.entity.InvoiceRule;
import lombok.Data;

@Data
public class InvoiceRuleUpdateRequest {

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
}

