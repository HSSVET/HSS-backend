package com.hss.hss_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationCreateRequest {
  private Long animalId;
  private Long vaccineId;
  private LocalDate date;
  private LocalDate nextDueDate;
  private String veterinarianName;
  private String notes;

  // Inventory integration
  private Long stockProductId;
  private Boolean deductStock;
}
