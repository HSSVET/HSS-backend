package com.hss.hss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryMedicationDto {
  private Long surgeryMedId;
  private Long surgeryId;
  private Long medicineId;
  private Integer quantity;
  private String notes;
}
