package com.hss.hss_backend.dto;

import com.hss.hss_backend.entity.LabTest;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LabTestDTO {
  private Long testId;
  private Long animalId;
  private String animalName;
  private String animalSpecies;
  private String testName;
  private LocalDate date;
  private LabTest.Status status;
}
