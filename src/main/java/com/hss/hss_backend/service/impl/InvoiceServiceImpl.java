package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.entity.*;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.repository.*;
import com.hss.hss_backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

  private final InvoiceRepository invoiceRepository;
  private final SurgeryRepository surgeryRepository;
  private final HospitalizationRepository hospitalizationRepository;

  @Override
  public Invoice createInvoiceForSurgery(Long surgeryId) {
    Surgery surgery = surgeryRepository.findById(surgeryId)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found"));

    if (!"COMPLETED".equals(surgery.getStatus())) {
      // Maybe allow invoicing before completion? For now enforce completion.
      // throw new IllegalStateException("Surgery must be completed to generate
      // invoice");
    }

    Invoice invoice = new Invoice();
    invoice.setOwner(surgery.getAnimal().getOwner());
    invoice.setDate(LocalDate.now());
    invoice.setInvoiceItems(new ArrayList<>());
    invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8)); // Simple generation

    // Add Surgery Fee (Base)
    InvoiceItem surgeryItem = new InvoiceItem();
    surgeryItem.setInvoice(invoice);
    surgeryItem.setDescription("Surgery: " + surgery.getAnimal().getName()); // Better desc needed
    surgeryItem.setQuantity(1);
    surgeryItem.setUnitPrice(new BigDecimal("1000.00")); // Placeholder price, should come from a
                                                         // PriceList/ServiceEntity
    surgeryItem.setLineTotal(surgeryItem.getUnitPrice());
    surgeryItem.setItemType(InvoiceItem.ItemType.SURGERY);

    invoice.getInvoiceItems().add(surgeryItem);

    // Add Medications
    for (SurgeryMedication med : surgery.getMedications()) {
      InvoiceItem medItem = new InvoiceItem();
      medItem.setInvoice(invoice);
      medItem.setDescription("Medication: " + med.getMedicineId()); // Need Medicine name ideally
      medItem.setQuantity(med.getQuantity());
      medItem.setUnitPrice(new BigDecimal("50.00")); // Placeholder
      medItem.setLineTotal(medItem.getUnitPrice().multiply(new BigDecimal(med.getQuantity())));
      medItem.setItemType(InvoiceItem.ItemType.MEDICINE);
      invoice.getInvoiceItems().add(medItem);
    }

    calculateTotals(invoice);
    return invoiceRepository.save(invoice);
  }

  @Override
  public Invoice createInvoiceForHospitalization(Long hospitalizationId) {
    Hospitalization hospitalization = hospitalizationRepository.findById(hospitalizationId)
        .orElseThrow(() -> new ResourceNotFoundException("Hospitalization not found"));

    if (hospitalization.getDischargeDate() == null) {
      // throw new IllegalStateException("Patient not discharged yet");
    }

    Invoice invoice = new Invoice();
    invoice.setOwner(hospitalization.getAnimal().getOwner());
    invoice.setDate(LocalDate.now());
    invoice.setInvoiceItems(new ArrayList<>());
    invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8));

    long days = 1;
    if (hospitalization.getAdmissionDate() != null && hospitalization.getDischargeDate() != null) {
      days = ChronoUnit.DAYS.between(hospitalization.getAdmissionDate(), hospitalization.getDischargeDate());
      if (days < 1)
        days = 1;
    }

    InvoiceItem stayItem = new InvoiceItem();
    stayItem.setInvoice(invoice);
    stayItem.setDescription("Hospitalization: " + days + " days");
    stayItem.setQuantity((int) days);
    stayItem.setUnitPrice(new BigDecimal("200.00")); // Placeholder daily rate
    stayItem.setLineTotal(stayItem.getUnitPrice().multiply(new BigDecimal(days)));
    stayItem.setItemType(InvoiceItem.ItemType.SERVICE); // or HOSPITALIZATION

    invoice.getInvoiceItems().add(stayItem);

    calculateTotals(invoice);
    return invoiceRepository.save(invoice);
  }

  private void calculateTotals(Invoice invoice) {
    BigDecimal total = invoice.getInvoiceItems().stream()
        .map(InvoiceItem::getLineTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    invoice.setTotalAmount(total); // ignoring tax logic for brevity
    invoice.setAmount(total);
  }
}
