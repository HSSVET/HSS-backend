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
public class HospitalizationDto {
  private Long hospitalizationId;
  private Long animalId;
  private String animalName;
  private LocalDateTime admissionDate;
  private LocalDateTime dischargeDate;
  private String status;
  private String primaryVeterinarian;
  private String diagnosisSummary;
  private String carePlan;
  private List<HospitalizationLogDto> logs;

  private LocalDateTime createdAt;
  private String createdBy;
}
