package com.hss.hss_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalExaminationCreateRequest {

    @NotNull(message = "Animal ID is required")
    private Long animalId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Findings are required")
    private String findings;

    @Size(max = 100, message = "Veterinarian name must not exceed 100 characters")
    private String veterinarianName;
}
