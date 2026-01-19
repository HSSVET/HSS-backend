package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResponse {
  private Long id;
  private Long serviceId;
  private ServiceResponse service;
  private Integer quantity;
  private BigDecimal unitPrice;
  private BigDecimal discount;
  private BigDecimal total;
}
