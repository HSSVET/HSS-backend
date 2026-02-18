package com.hss.hss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryDto {
  private Long surgeryId;
  private Long animalId;
  private String animalName;
  private Long veterinarianId;
  private LocalDateTime date;
  private String status;
  private String notes;
  private String preOpInstructions;
  private String postOpInstructions;
  private String anesthesiaProtocol;
  private Boolean anesthesiaConsent;
  private List<SurgeryMedicationDto> medications;

  private LocalDateTime createdAt;
  private String createdBy;
}
