package com.hss.hss_backend.dto.request;

import com.hss.hss_backend.entity.Treatment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentCreateRequest {

    @NotNull(message = "Animal ID is required")
    private Long animalId;

    @NotNull(message = "Treatment type is required")
    private Treatment.TreatmentType treatmentType;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    private String description;

    private String diagnosis;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private Treatment.TreatmentStatus status;

    @Size(max = 100, message = "Veterinarian name must not exceed 100 characters")
    private String veterinarianName;

    private String notes;

    @DecimalMin(value = "0.0", message = "Cost must be positive")
    private BigDecimal cost;
}
