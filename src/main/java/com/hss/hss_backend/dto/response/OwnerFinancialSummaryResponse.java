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
public class OwnerFinancialSummaryResponse {
  private Long ownerId;
  private BigDecimal totalInvoiced;
  private BigDecimal totalPaid;
  private BigDecimal balance;
  private BigDecimal overdueAmount;
}
