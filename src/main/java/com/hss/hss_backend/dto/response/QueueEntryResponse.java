package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueEntryResponse {
  private Long queueEntryId;
  private Long clinicId;
  private Long appointmentId;
  private Long animalId;
  private String animalName;
  private String ownerName;
  private Integer queueNumber;
  private String status; // WAITING, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW
  private String priority; // LOW, NORMAL, HIGH, URGENT, EMERGENCY
  private LocalDateTime checkInTime;
  private LocalDateTime estimatedStartTime;
  private Integer estimatedWaitMinutes;
  private Long assignedVeterinarianId;
  private String assignedVeterinarianName;
  private String assignedRoom;
  private String notes;
}
