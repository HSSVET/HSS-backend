package com.hss.hss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineScheduleDTO {

    private Long scheduleId;
    private Long animalId;
    private String animalName;
    private Long vaccineId;
    private String vaccineName;
    private String manufacturer;
    private LocalDate scheduledDate;
    private LocalDate dueDate;
    private String priority;
    private String status;
    private String notes;
    private String veterinarianName;
    private Boolean isCompleted;
    private Boolean isOverdue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
