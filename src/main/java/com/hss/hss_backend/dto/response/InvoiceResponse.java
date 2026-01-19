package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
  private Long id;
  private String invoiceNumber;
  private Long patientId;
  private PatientInfo patient;
  private LocalDate issueDate;
  private LocalDate dueDate;
  private List<InvoiceItemResponse> items;
  private BigDecimal subtotal;
  private BigDecimal tax;
  private BigDecimal discount;
  private BigDecimal total;
  private String status;
  private String notes;
  private String createdBy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PatientInfo {
    private Long id;
    private String name;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
  }
}
