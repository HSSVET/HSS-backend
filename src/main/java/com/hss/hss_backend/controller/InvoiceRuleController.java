package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.InvoiceRuleCreateRequest;
import com.hss.hss_backend.dto.request.InvoiceRuleUpdateRequest;
import com.hss.hss_backend.dto.response.InvoiceRuleResponse;
import com.hss.hss_backend.service.InvoiceRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoice-rules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Invoice Rule", description = "Invoice rule management APIs")
public class InvoiceRuleController {

    private final InvoiceRuleService ruleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Create a new invoice rule")
    public ResponseEntity<InvoiceRuleResponse> createRule(@Valid @RequestBody InvoiceRuleCreateRequest request) {
        log.info("Creating invoice rule: {}", request.getRuleName());
        InvoiceRuleResponse response = ruleService.createRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get all invoice rules")
    public ResponseEntity<List<InvoiceRuleResponse>> getAllRules() {
        log.info("Fetching all invoice rules");
        List<InvoiceRuleResponse> rules = ruleService.getAllRules();
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get all active invoice rules")
    public ResponseEntity<List<InvoiceRuleResponse>> getActiveRules() {
        log.info("Fetching active invoice rules");
        List<InvoiceRuleResponse> rules = ruleService.getActiveRules();
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get invoice rule by ID")
    public ResponseEntity<InvoiceRuleResponse> getRuleById(@PathVariable Long id) {
        log.info("Fetching invoice rule with ID: {}", id);
        InvoiceRuleResponse rule = ruleService.getRuleById(id);
        return ResponseEntity.ok(rule);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Update an invoice rule")
    public ResponseEntity<InvoiceRuleResponse> updateRule(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceRuleUpdateRequest request) {
        log.info("Updating invoice rule with ID: {}", id);
        InvoiceRuleResponse response = ruleService.updateRule(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an invoice rule")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        log.info("Deleting invoice rule with ID: {}", id);
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/process")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Manually process all invoice rules (Admin only)")
    public ResponseEntity<Void> processAllRules() {
        log.info("Manually processing all invoice rules");
        ruleService.processAllRules();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/appointment/{appointmentId}/process")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Process invoice rules for a specific appointment")
    public ResponseEntity<Void> processRulesForAppointment(@PathVariable Long appointmentId) {
        log.info("Processing invoice rules for appointment ID: {}", appointmentId);
        ruleService.processRulesForAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }
}

