package com.hss.hss_backend.dto.response;

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
public class AnimalResponse {

    private Long animalId;
    private Long ownerId;
    private String ownerName;
    private String name;
    private Long speciesId;
    private String speciesName;
    private Long breedId;
    private String breedName;
    private String gender;
    private LocalDate birthDate;
    private BigDecimal weight;
    private String color;
    private String microchipNo;
    private String allergies;
    private String chronicDiseases;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
