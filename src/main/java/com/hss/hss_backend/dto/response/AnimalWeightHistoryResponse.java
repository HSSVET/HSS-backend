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
public class AnimalWeightHistoryResponse {
  private Long id;
  private Long animalId;
  private BigDecimal weight;
  private LocalDate measuredAt;
  private String note;
  private String createdBy;
}
