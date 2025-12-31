package com.hss.hss_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hss.hss_backend.dto.request.InvoiceRuleCreateRequest;
import com.hss.hss_backend.dto.request.InvoiceRuleUpdateRequest;
import com.hss.hss_backend.dto.response.InvoiceRuleResponse;
import com.hss.hss_backend.entity.*;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceRuleService {

    private final InvoiceRuleRepository ruleRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final AppointmentRepository appointmentRepository;
    private final ObjectMapper objectMapper;

    public InvoiceRuleResponse createRule(InvoiceRuleCreateRequest request) {
        log.info("Creating invoice rule: {}", request.getRuleName());

        InvoiceRule rule = InvoiceRule.builder()
                .ruleName(request.getRuleName())
                .ruleType(request.getRuleType())
                .triggerEntity(request.getTriggerEntity())
                .triggerStatus(request.getTriggerStatus())
                .conditions(request.getConditions())
                .invoiceTemplate(request.getInvoiceTemplate())
                .dueDays(request.getDueDays() != null ? request.getDueDays() : 30)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .description(request.getDescription())
                .notes(request.getNotes())
                .build();

        InvoiceRule savedRule = ruleRepository.save(rule);
        log.info("Invoice rule created successfully with ID: {}", savedRule.getRuleId());
        return toResponse(savedRule);
    }

    @Transactional(readOnly = true)
    public InvoiceRuleResponse getRuleById(Long id) {
        log.info("Fetching invoice rule with ID: {}", id);
        InvoiceRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvoiceRule", id));
        return toResponse(rule);
    }

    @Transactional(readOnly = true)
    public List<InvoiceRuleResponse> getAllRules() {
        log.info("Fetching all invoice rules");
        List<InvoiceRule> rules = ruleRepository.findAll();
        return rules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvoiceRuleResponse> getActiveRules() {
        log.info("Fetching active invoice rules");
        List<InvoiceRule> rules = ruleRepository.findActiveRules();
        return rules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public InvoiceRuleResponse updateRule(Long id, InvoiceRuleUpdateRequest request) {
        log.info("Updating invoice rule with ID: {}", id);
        InvoiceRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvoiceRule", id));

        if (request.getRuleName() != null) {
            rule.setRuleName(request.getRuleName());
        }
        if (request.getRuleType() != null) {
            rule.setRuleType(request.getRuleType());
        }
        if (request.getTriggerEntity() != null) {
            rule.setTriggerEntity(request.getTriggerEntity());
        }
        if (request.getTriggerStatus() != null) {
            rule.setTriggerStatus(request.getTriggerStatus());
        }
        if (request.getConditions() != null) {
            rule.setConditions(request.getConditions());
        }
        if (request.getInvoiceTemplate() != null) {
            rule.setInvoiceTemplate(request.getInvoiceTemplate());
        }
        if (request.getDueDays() != null) {
            rule.setDueDays(request.getDueDays());
        }
        if (request.getIsActive() != null) {
            rule.setIsActive(request.getIsActive());
        }
        if (request.getPriority() != null) {
            rule.setPriority(request.getPriority());
        }
        if (request.getDescription() != null) {
            rule.setDescription(request.getDescription());
        }
        if (request.getNotes() != null) {
            rule.setNotes(request.getNotes());
        }

        InvoiceRule updatedRule = ruleRepository.save(rule);
        log.info("Invoice rule updated successfully");
        return toResponse(updatedRule);
    }

    public void deleteRule(Long id) {
        log.info("Deleting invoice rule with ID: {}", id);
        InvoiceRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvoiceRule", id));
        ruleRepository.delete(rule);
        log.info("Invoice rule deleted successfully");
    }

    public void processRulesForAppointment(Long appointmentId) {
        log.info("Processing invoice rules for appointment ID: {}", appointmentId);
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        List<InvoiceRule> rules = ruleRepository.findActiveRulesByTriggerEntity("APPOINTMENT");
        
        for (InvoiceRule rule : rules) {
            if (shouldApplyRule(rule, appointment)) {
                createInvoiceFromRule(rule, appointment);
            }
        }
    }

    private boolean shouldApplyRule(InvoiceRule rule, Appointment appointment) {
        // Trigger status kontrolü
        if (rule.getTriggerStatus() != null && !rule.getTriggerStatus().equals(appointment.getStatus().name())) {
            return false;
        }

        // Conditions kontrolü (JSON)
        if (rule.getConditions() != null && !rule.getConditions().isEmpty()) {
            try {
                Map<String, Object> conditions = objectMapper.readValue(rule.getConditions(), 
                    new TypeReference<Map<String, Object>>() {});
                
                // Örnek: appointment type kontrolü
                if (conditions.containsKey("appointmentType")) {
                    // Appointment entity'de type yoksa bu kontrolü atla
                }
            } catch (Exception e) {
                log.warn("Error parsing rule conditions: {}", e.getMessage());
            }
        }

        return true;
    }

    private void createInvoiceFromRule(InvoiceRule rule, Appointment appointment) {
        try {
            Owner owner = appointment.getAnimal().getOwner();
            
            // Fatura numarası oluştur
            String invoiceNumber = generateInvoiceNumber();
            
            // Fatura tarihi ve vade tarihi
            LocalDate invoiceDate = LocalDate.now();
            LocalDate dueDate = invoiceDate.plusDays(rule.getDueDays());
            
            // Invoice oluştur
            Invoice invoice = Invoice.builder()
                    .owner(owner)
                    .invoiceNumber(invoiceNumber)
                    .date(invoiceDate)
                    .dueDate(dueDate)
                    .status(Invoice.Status.PENDING)
                    .description(rule.getDescription() != null ? rule.getDescription() : 
                        String.format("Otomatik oluşturulan fatura - Kural: %s", rule.getRuleName()))
                    .notes(rule.getNotes())
                    .build();

            Invoice savedInvoice = invoiceRepository.save(invoice);

            // Invoice items oluştur
            List<InvoiceItem> items = createInvoiceItemsFromTemplate(rule, savedInvoice, appointment);
            invoiceItemRepository.saveAll(items);

            // Fatura toplamlarını hesapla
            calculateInvoiceTotals(savedInvoice, items);
            invoiceRepository.save(savedInvoice);

            log.info("Invoice created from rule {} for appointment {}", rule.getRuleId(), appointment.getAppointmentId());
        } catch (Exception e) {
            log.error("Error creating invoice from rule {}: {}", rule.getRuleId(), e.getMessage(), e);
        }
    }

    private List<InvoiceItem> createInvoiceItemsFromTemplate(InvoiceRule rule, Invoice invoice, Appointment appointment) {
        List<InvoiceItem> items = new ArrayList<>();

        if (rule.getInvoiceTemplate() == null || rule.getInvoiceTemplate().isEmpty()) {
            // Varsayılan item oluştur
            InvoiceItem defaultItem = InvoiceItem.builder()
                    .invoice(invoice)
                    .description(String.format("Randevu - %s", appointment.getSubject()))
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(100.00))
                    .vatRate(BigDecimal.valueOf(18.00))
                    .itemType(InvoiceItem.ItemType.CONSULTATION)
                    .build();
            
            defaultItem.setVatAmount(defaultItem.getUnitPrice()
                    .multiply(defaultItem.getVatRate())
                    .divide(BigDecimal.valueOf(100)));
            defaultItem.setLineTotal(defaultItem.getUnitPrice().add(defaultItem.getVatAmount()));
            
            items.add(defaultItem);
            return items;
        }

        try {
            List<Map<String, Object>> templateItems = objectMapper.readValue(rule.getInvoiceTemplate(),
                    new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> templateItem : templateItems) {
                InvoiceItem item = InvoiceItem.builder()
                        .invoice(invoice)
                        .description((String) templateItem.getOrDefault("description", "Hizmet"))
                        .quantity(((Number) templateItem.getOrDefault("quantity", 1)).intValue())
                        .unitPrice(BigDecimal.valueOf(((Number) templateItem.getOrDefault("unitPrice", 0)).doubleValue()))
                        .vatRate(BigDecimal.valueOf(((Number) templateItem.getOrDefault("vatRate", 18)).doubleValue()))
                        .itemType(InvoiceItem.ItemType.valueOf(
                                (String) templateItem.getOrDefault("itemType", "SERVICE")))
                        .build();

                item.setVatAmount(item.getUnitPrice()
                        .multiply(item.getVatRate())
                        .divide(BigDecimal.valueOf(100)));
                item.setLineTotal(item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
                        .add(item.getVatAmount()));

                items.add(item);
            }
        } catch (Exception e) {
            log.error("Error parsing invoice template: {}", e.getMessage());
            // Varsayılan item oluştur
            InvoiceItem defaultItem = InvoiceItem.builder()
                    .invoice(invoice)
                    .description("Hizmet")
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(100.00))
                    .vatRate(BigDecimal.valueOf(18.00))
                    .itemType(InvoiceItem.ItemType.SERVICE)
                    .build();
            
            defaultItem.setVatAmount(defaultItem.getUnitPrice()
                    .multiply(defaultItem.getVatRate())
                    .divide(BigDecimal.valueOf(100)));
            defaultItem.setLineTotal(defaultItem.getUnitPrice().add(defaultItem.getVatAmount()));
            
            items.add(defaultItem);
        }

        return items;
    }

    private void calculateInvoiceTotals(Invoice invoice, List<InvoiceItem> items) {
        BigDecimal amount = items.stream()
                .map(InvoiceItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxAmount = items.stream()
                .map(InvoiceItem::getVatAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setAmount(amount.subtract(taxAmount));
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(amount);
    }

    private String generateInvoiceNumber() {
        String prefix = "INV";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%04d", new Random().nextInt(10000));
        return String.format("%s-%s-%s", prefix, dateStr, randomStr);
    }

    public void processAllRules() {
        log.info("Processing all active invoice rules");
        List<InvoiceRule> activeRules = ruleRepository.findActiveRules();
        
        for (InvoiceRule rule : activeRules) {
            try {
                processRule(rule);
            } catch (Exception e) {
                log.error("Error processing rule {}: {}", rule.getRuleId(), e.getMessage(), e);
            }
        }
    }

    private void processRule(InvoiceRule rule) {
        switch (rule.getRuleType()) {
            case APPOINTMENT_AFTER:
                processAppointmentRules(rule);
                break;
            case MONTHLY_SUBSCRIPTION:
                processMonthlySubscriptionRules(rule);
                break;
            // Diğer rule tipleri için implementasyonlar eklenebilir
            default:
                log.warn("Rule type {} not yet implemented", rule.getRuleType());
        }
    }

    private void processAppointmentRules(InvoiceRule rule) {
        // Son 24 saat içinde tamamlanan randevuları bul
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Appointment> completedAppointments = appointmentRepository
                .findByStatusAndDateTimeBetween(
                    Appointment.Status.COMPLETED,
                    yesterday.atStartOfDay(),
                    LocalDate.now().atTime(23, 59, 59));

        for (Appointment appointment : completedAppointments) {
            // Bu randevu için zaten fatura oluşturulmuş mu kontrol et
            if (!hasInvoiceForAppointment(appointment)) {
                if (shouldApplyRule(rule, appointment)) {
                    createInvoiceFromRule(rule, appointment);
                }
            }
        }
    }

    private void processMonthlySubscriptionRules(InvoiceRule rule) {
        // Aylık abonelik kuralları için implementasyon
        log.info("Processing monthly subscription rules - not yet implemented");
    }

    private boolean hasInvoiceForAppointment(Appointment appointment) {
        // Invoice'da appointment referansı yok, description veya notes'da kontrol edilebilir
        // Şimdilik false döndürüyoruz
        return false;
    }

    private InvoiceRuleResponse toResponse(InvoiceRule rule) {
        return InvoiceRuleResponse.builder()
                .ruleId(rule.getRuleId())
                .ruleName(rule.getRuleName())
                .ruleType(rule.getRuleType())
                .triggerEntity(rule.getTriggerEntity())
                .triggerStatus(rule.getTriggerStatus())
                .conditions(rule.getConditions())
                .invoiceTemplate(rule.getInvoiceTemplate())
                .dueDays(rule.getDueDays())
                .isActive(rule.getIsActive())
                .priority(rule.getPriority())
                .description(rule.getDescription())
                .notes(rule.getNotes())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}

