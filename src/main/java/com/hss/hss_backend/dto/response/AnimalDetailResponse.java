package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalDetailResponse {

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
    private String status;
    private String behaviorNotes;
    private String profileImageUrl;
    private Double height;
    private Boolean sterilized;
    private java.util.List<com.hss.hss_backend.dto.AnimalConditionDTO> conditions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional details
    private List<AppointmentResponse> recentAppointments;
    private List<MedicalHistoryResponse> recentMedicalHistory;
    private List<VaccinationResponse> recentVaccinations;
    private List<LabTestResponse> recentLabTests;
}
