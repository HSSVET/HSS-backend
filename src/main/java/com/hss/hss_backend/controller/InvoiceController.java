package com.hss.hss_backend.controller;

import com.hss.hss_backend.entity.Invoice;
import com.hss.hss_backend.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "APIs for managing invoices")
public class InvoiceController {

  private final InvoiceService invoiceService;

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
