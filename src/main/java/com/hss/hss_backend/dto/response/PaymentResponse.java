package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
  private Long paymentId;
  private Long ownerId;
  private Long invoiceId;
  private BigDecimal amount;
  private LocalDate paymentDate;
  private String paymentMethod;
  private String notes;
}
