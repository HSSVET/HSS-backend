package com.hss.hss_backend.dto.response;

import com.hss.hss_backend.entity.Treatment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentResponse {

    private Long treatmentId;
    private Long animalId;
    private String animalName;
    private Long clinicId;
    private Treatment.TreatmentType treatmentType;
    private String title;
    private String description;
    private String diagnosis;
    private LocalDate startDate;
    private LocalDate endDate;
    private Treatment.TreatmentStatus status;
    private String veterinarianName;
    private String notes;
    private BigDecimal cost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
