package com.hss.hss_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateLabTestRequest {
  private Long animalId;
  private Long veterinarianId;
  private String testName;
  private String category;
  private String notes;
  private boolean urgent;
}
