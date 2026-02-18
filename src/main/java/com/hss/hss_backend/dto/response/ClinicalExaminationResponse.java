package com.hss.hss_backend.dto.response;

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
public class ClinicalExaminationResponse {

    private Long examinationId;
    private Long animalId;
    private String animalName;
    private LocalDate date;
    private String findings;
    private String veterinarianName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
