package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.InvoiceCreateRequest;
import com.hss.hss_backend.dto.request.ServiceCreateRequest;
import com.hss.hss_backend.dto.response.InvoiceResponse;
import com.hss.hss_backend.dto.response.PaymentResponse;
import com.hss.hss_backend.dto.response.ServiceResponse;
import com.hss.hss_backend.entity.Invoice;
import com.hss.hss_backend.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@Tag(name = "Billing Management", description = "APIs for managing invoices, payments, and services")
public class InvoiceController {

  private final InvoiceService invoiceService;

  // Invoice endpoints
  @GetMapping
  @Operation(summary = "Get all invoices")
  public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
    return ResponseEntity.ok(invoiceService.getAllInvoices());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get invoice by ID")
  public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
    return ResponseEntity.ok(invoiceService.getInvoiceById(id));
  }

  @PostMapping
  @Operation(summary = "Create new invoice")
  public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(request));
  }

  // Payment endpoints
  @GetMapping("/payments")
  @Operation(summary = "Get all payments")
  public ResponseEntity<List<PaymentResponse>> getAllPayments() {
    return ResponseEntity.ok(invoiceService.getAllPayments());
  }

  @PostMapping("/payments")
  @Operation(summary = "Create new payment")
  public ResponseEntity<PaymentResponse> createPayment(
      @RequestParam Long invoiceId,
      @Valid @RequestBody PaymentResponse paymentData) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(invoiceService.createPayment(invoiceId, paymentData));
  }

  // Service endpoints
  @GetMapping("/services")
  @Operation(summary = "Get all services")
  public ResponseEntity<List<ServiceResponse>> getAllServices() {
    return ResponseEntity.ok(invoiceService.getAllServices());
  }

  @PostMapping("/services")
  @Operation(summary = "Create new service")
  public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody ServiceCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createService(request));
  }

  // Existing invoice generation endpoints
  @PostMapping("/surgery/{surgeryId}")
  @Operation(summary = "Generate invoice from Surgery")
  public ResponseEntity<Invoice> createFromSurgery(@PathVariable Long surgeryId) {
    return ResponseEntity.ok(invoiceService.createInvoiceForSurgery(surgeryId));
  }

  @PostMapping("/hospitalization/{hospitalizationId}")
  @Operation(summary = "Generate invoice from Hospitalization")
  public ResponseEntity<Invoice> createFromHospitalization(@PathVariable Long hospitalizationId) {
    return ResponseEntity.ok(invoiceService.createInvoiceForHospitalization(hospitalizationId));
  }
}
