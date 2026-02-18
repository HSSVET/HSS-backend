package com.hss.hss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalConditionDTO {
  private Long id;
  private Long animalId;
  private String type; // ALLERGY, CHRONIC_CONDITION
  private String name;
  private String severity; // MILD, MODERATE, SEVERE
  private LocalDate diagnosisDate;
  private String diagnosedBy;
  private String status; // ACTIVE, MANAGED, RESOLVED
  private String notes;
}
