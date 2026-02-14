package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalStatisticsResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalAnimals;
    private Long totalAppointments;
    private Long completedAppointments;
    private Long cancelledAppointments;
    private Long totalVaccinations;
    private Long upcomingVaccinations;
    private Long overdueVaccinations;
    private Map<String, Long> animalsBySpecies;
    private Map<String, Long> animalsByBreed;
    private Map<String, Long> appointmentsByType;
    private List<Map<String, Object>> mostCommonTreatments;
    private Map<String, Long> vaccinationsByType;
}

