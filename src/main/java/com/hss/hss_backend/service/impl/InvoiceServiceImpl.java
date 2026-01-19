package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.dto.request.InvoiceCreateRequest;
import com.hss.hss_backend.dto.request.ServiceCreateRequest;
import com.hss.hss_backend.dto.response.InvoiceResponse;
import com.hss.hss_backend.dto.response.PaymentResponse;
import com.hss.hss_backend.dto.response.ServiceResponse;
import com.hss.hss_backend.entity.*;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.InvoiceMapper;
import com.hss.hss_backend.mapper.PaymentMapper;
import com.hss.hss_backend.repository.*;
import com.hss.hss_backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

  private final InvoiceRepository invoiceRepository;
  private final SurgeryRepository surgeryRepository;
  private final HospitalizationRepository hospitalizationRepository;
  private final PaymentRepository paymentRepository;
  private final VetServiceRepository vetServiceRepository;
  private final AnimalRepository animalRepository;
  private final OwnerRepository ownerRepository;
  private final InvoiceMapper invoiceMapper;
  private final PaymentMapper paymentMapper;

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

  // New methods for billing endpoints
  @Override
  public List<InvoiceResponse> getAllInvoices() {
    return invoiceRepository.findAll().stream()
        .map(invoiceMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public InvoiceResponse getInvoiceById(Long id) {
    Invoice invoice = invoiceRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    return invoiceMapper.toResponse(invoice);
  }

  @Override
  public InvoiceResponse createInvoice(InvoiceCreateRequest request) {
    Animal animal = animalRepository.findById(request.getPatientId())
        .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

    Invoice invoice = new Invoice();
    invoice.setOwner(animal.getOwner());
    invoice.setDate(request.getIssueDate());
    invoice.setDueDate(request.getDueDate());
    invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8));
    invoice.setStatus(Invoice.Status.PENDING);
    invoice.setInvoiceItems(new ArrayList<>());

    // Create invoice items
    for (InvoiceCreateRequest.InvoiceItemRequest itemReq : request.getItems()) {
      VetService service = vetServiceRepository.findById(itemReq.getServiceId())
          .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

      InvoiceItem item = new InvoiceItem();
      item.setInvoice(invoice);
      item.setDescription(service.getName());
      item.setQuantity(itemReq.getQuantity());
      item.setUnitPrice(service.getPrice());
      item.setItemType(InvoiceItem.ItemType.SERVICE);
      
      BigDecimal lineTotal = service.getPrice().multiply(new BigDecimal(itemReq.getQuantity()));
      if (itemReq.getDiscount() != null) {
        lineTotal = lineTotal.subtract(itemReq.getDiscount());
      }
      item.setLineTotal(lineTotal);
      
      invoice.getInvoiceItems().add(item);
    }

    calculateTotals(invoice);
    if (request.getDiscount() != null) {
      invoice.setTotalAmount(invoice.getTotalAmount().subtract(request.getDiscount()));
    }
    invoice.setNotes(request.getNotes());

    Invoice saved = invoiceRepository.save(invoice);
    return invoiceMapper.toResponse(saved);
  }

  @Override
  public List<PaymentResponse> getAllPayments() {
    return paymentRepository.findAll().stream()
        .map(paymentMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public PaymentResponse createPayment(Long invoiceId, PaymentResponse paymentData) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
        .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

    Payment payment = new Payment();
    payment.setInvoice(invoice);
    payment.setOwner(invoice.getOwner());
    payment.setAmount(paymentData.getAmount());
    payment.setPaymentDate(LocalDate.now());
    payment.setPaymentMethod(paymentData.getPaymentMethod());
    payment.setNotes(paymentData.getNotes());

    // Update invoice status if fully paid
    BigDecimal totalPaid = invoice.getPaidAmount() != null ? 
        invoice.getPaidAmount().add(paymentData.getAmount()) : paymentData.getAmount();
    invoice.setPaidAmount(totalPaid);
    
    if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
      invoice.setStatus(Invoice.Status.PAID);
      invoice.setPaymentDate(LocalDate.now());
    }

    Payment saved = paymentRepository.save(payment);
    invoiceRepository.save(invoice);
    
    return paymentMapper.toResponse(saved);
  }

  @Override
  public List<ServiceResponse> getAllServices() {
    return vetServiceRepository.findAll().stream()
        .map(service -> ServiceResponse.builder()
            .id(service.getServiceId())
            .name(service.getName())
            .description(service.getDescription())
            .price(service.getPrice())
            .category(service.getCategory())
            .build())
        .collect(Collectors.toList());
  }

  @Override
  public ServiceResponse createService(ServiceCreateRequest request) {
    VetService service = new VetService();
    service.setName(request.getName());
    service.setDescription(request.getDescription());
    service.setPrice(request.getPrice());
    service.setCategory(request.getCategory());

    VetService saved = vetServiceRepository.save(service);
    
    return ServiceResponse.builder()
        .id(saved.getServiceId())
        .name(saved.getName())
        .description(saved.getDescription())
        .price(saved.getPrice())
        .category(saved.getCategory())
        .build();
  }
}
