package com.hss.hss_backend.dto;

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
public class AnimalDTO {

    private Long animalId;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
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
    private LocalDate lastCheckup;
    private LocalDate nextVaccine;
    private String healthStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
