package com.hss.hss_backend.dto.request;

import com.hss.hss_backend.entity.Treatment;
import jakarta.validation.constraints.DecimalMin;
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
public class TreatmentUpdateRequest {

    private Treatment.TreatmentType treatmentType;

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    private String description;

    private String diagnosis;

    private LocalDate startDate;

    private LocalDate endDate;

    private Treatment.TreatmentStatus status;

    @Size(max = 100, message = "Veterinarian name must not exceed 100 characters")
    private String veterinarianName;

    private String notes;

    @DecimalMin(value = "0.0", message = "Cost must be positive")
    private BigDecimal cost;
}
