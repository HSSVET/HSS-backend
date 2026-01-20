package com.hss.hss_backend.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class AnimalWeightHistoryRequest {

  @NotNull(message = "Weight is required")
  @DecimalMin(value = "0.0", message = "Weight must be positive")
  @DecimalMax(value = "999.99", message = "Weight must not exceed 999.99")
  private BigDecimal weight;

  @NotNull(message = "Date is required")
  private LocalDate measuredAt;

  @Size(max = 500, message = "Note must not exceed 500 characters")
  private String note;
}
