package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.response.InvoiceItemResponse;
import com.hss.hss_backend.dto.response.InvoiceResponse;
import com.hss.hss_backend.dto.response.ServiceResponse;
import com.hss.hss_backend.entity.Invoice;
import com.hss.hss_backend.entity.InvoiceItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class InvoiceMapper {

  public InvoiceResponse toResponse(Invoice invoice) {
    if (invoice == null) {
      return null;
    }

    return InvoiceResponse.builder()
        .id(invoice.getInvoiceId())
        .invoiceNumber(invoice.getInvoiceNumber())
        .patientId(invoice.getOwner() != null ? invoice.getOwner().getOwnerId() : null)
        .patient(toPatientInfo(invoice))
        .issueDate(invoice.getDate())
        .dueDate(invoice.getDueDate())
        .items(invoice.getInvoiceItems() != null ? 
            invoice.getInvoiceItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList()) : null)
        .subtotal(invoice.getAmount())
        .tax(invoice.getTaxAmount())
        .discount(java.math.BigDecimal.ZERO)
        .total(invoice.getTotalAmount())
        .status(invoice.getStatus() != null ? invoice.getStatus().name() : null)
        .notes(invoice.getNotes())
        .createdBy(invoice.getCreatedBy())
        .createdAt(invoice.getCreatedAt())
        .updatedAt(invoice.getUpdatedAt())
        .build();
  }

  private InvoiceResponse.PatientInfo toPatientInfo(Invoice invoice) {
    if (invoice.getOwner() == null) {
      return null;
    }

    return InvoiceResponse.PatientInfo.builder()
        .id(invoice.getOwner().getOwnerId())
        .name("") // Owner name - need to get from animal if available
        .ownerName(invoice.getOwner().getFirstName() + " " + invoice.getOwner().getLastName())
        .ownerPhone(invoice.getOwner().getPhone())
        .ownerEmail(invoice.getOwner().getEmail())
        .build();
  }

  private InvoiceItemResponse toItemResponse(InvoiceItem item) {
    if (item == null) {
      return null;
    }

    return InvoiceItemResponse.builder()
        .id(item.getInvoiceItemId())
        .serviceId(null) // Not available in InvoiceItem
        .service(ServiceResponse.builder()
            .id(null)
            .name(item.getDescription())
            .price(item.getUnitPrice())
            .build())
        .quantity(item.getQuantity())
        .unitPrice(item.getUnitPrice())
        .discount(java.math.BigDecimal.ZERO)
        .total(item.getLineTotal())
        .build();
  }
}
