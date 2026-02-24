package com.hss.hss_backend.dto.response;

import com.hss.hss_backend.entity.VaccinationSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationScheduleResponse {

    private Long scheduleId;
    private Long animalId;
    private String animalName;
    private Long vaccineId;
    private String vaccineName;
    private Long protocolId;
    private String protocolName;
    private LocalDate scheduledDate;
    private Integer doseNumber;
    private VaccinationSchedule.Status status;
    private VaccinationSchedule.Priority priority;
    private Boolean isOverdue;
    private LocalDate completedDate;
    private Long vaccinationRecordId;
    private String notes;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}

