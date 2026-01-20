package com.hss.hss_backend.dto.request;

import jakarta.validation.constraints.*;
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
public class AnimalCreateRequest {

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotBlank(message = "Animal name is required")
    @Size(max = 100, message = "Animal name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Species ID is required")
    private Long speciesId;

    @NotNull(message = "Breed ID is required")
    private Long breedId;

    private String gender;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @DecimalMin(value = "0.0", message = "Weight must be positive")
    @DecimalMax(value = "999.99", message = "Weight must not exceed 999.99")
    private BigDecimal weight;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    @Size(max = 50, message = "Microchip number must not exceed 50 characters")
    private String microchipNo;

    @Size(max = 1000, message = "Allergies must not exceed 1000 characters")
    private String allergies;

    @Size(max = 1000, message = "Chronic diseases must not exceed 1000 characters")
    private String chronicDiseases;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    private String status;

    @Size(max = 2000, message = "Behavior notes must not exceed 2000 characters")
    private String behaviorNotes;

    private String profileImageUrl;

    @DecimalMin(value = "0.0", message = "Height must be positive")
    private Double height;

    private Boolean sterilized;
}
