package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.InvoiceRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRuleRepository extends JpaRepository<InvoiceRule, Long> {

    List<InvoiceRule> findByRuleType(InvoiceRule.RuleType ruleType);

    List<InvoiceRule> findByIsActive(Boolean isActive);

    @Query("SELECT ir FROM InvoiceRule ir WHERE ir.isActive = true")
    List<InvoiceRule> findActiveRules();

    @Query("SELECT ir FROM InvoiceRule ir WHERE ir.ruleType = :ruleType AND ir.isActive = true")
    List<InvoiceRule> findActiveRulesByType(@Param("ruleType") InvoiceRule.RuleType ruleType);

    @Query("SELECT ir FROM InvoiceRule ir WHERE ir.triggerEntity = :triggerEntity AND ir.isActive = true")
    List<InvoiceRule> findActiveRulesByTriggerEntity(@Param("triggerEntity") String triggerEntity);
}

