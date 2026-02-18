package com.hss.hss_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueCheckInRequest {
  private Long appointmentId; // Optional: if patient has appointment
  private Long animalId; // Required for walk-ins without appointment
  private String appointmentType; // Required for walk-ins: GENERAL_EXAM, VACCINATION, SURGERY, etc.
  private String priority; // Optional: NORMAL, HIGH, URGENT, EMERGENCY
  private String notes; // Optional notes
}
