package com.hss.hss_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreateRequest {
  
  @NotNull(message = "Patient ID is required")
  private Long patientId;
  
  @NotNull(message = "Issue date is required")
  private LocalDate issueDate;
  
  private LocalDate dueDate;
  
  @NotNull(message = "Items are required")
  private List<InvoiceItemRequest> items;
  
  private BigDecimal discount;
  private String notes;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class InvoiceItemRequest {
    @NotNull(message = "Service ID is required")
    private Long serviceId;
    
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    
    private BigDecimal discount;
  }
}
