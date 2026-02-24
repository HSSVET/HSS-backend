package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.request.TreatmentCreateRequest;
import com.hss.hss_backend.dto.request.TreatmentUpdateRequest;
import com.hss.hss_backend.dto.response.TreatmentResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Clinic;
import com.hss.hss_backend.entity.Treatment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TreatmentMapper {

    public static Treatment toEntity(TreatmentCreateRequest request, Animal animal, Clinic clinic) {
        return Treatment.builder()
                .animal(animal)
                .clinic(clinic)
                .treatmentType(request.getTreatmentType())
                .title(request.getTitle())
                .description(request.getDescription())
                .diagnosis(request.getDiagnosis())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus() != null ? request.getStatus() : Treatment.TreatmentStatus.ONGOING)
                .veterinarianName(request.getVeterinarianName())
                .notes(request.getNotes())
                .cost(request.getCost())
                .build();
    }

    public static void updateEntity(Treatment treatment, TreatmentUpdateRequest request) {
        if (request.getTreatmentType() != null) {
            treatment.setTreatmentType(request.getTreatmentType());
        }
        if (request.getTitle() != null) {
            treatment.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            treatment.setDescription(request.getDescription());
        }
        if (request.getDiagnosis() != null) {
            treatment.setDiagnosis(request.getDiagnosis());
        }
        if (request.getStartDate() != null) {
            treatment.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            treatment.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            treatment.setStatus(request.getStatus());
        }
        if (request.getVeterinarianName() != null) {
            treatment.setVeterinarianName(request.getVeterinarianName());
        }
        if (request.getNotes() != null) {
            treatment.setNotes(request.getNotes());
        }
        if (request.getCost() != null) {
            treatment.setCost(request.getCost());
        }
    }

    public static TreatmentResponse toResponse(Treatment treatment) {
        return TreatmentResponse.builder()
                .treatmentId(treatment.getTreatmentId())
                .animalId(treatment.getAnimal().getAnimalId())
                .animalName(treatment.getAnimal().getName())
                .clinicId(treatment.getClinic().getClinicId())
                .treatmentType(treatment.getTreatmentType())
                .title(treatment.getTitle())
                .description(treatment.getDescription())
                .diagnosis(treatment.getDiagnosis())
                .startDate(treatment.getStartDate())
                .endDate(treatment.getEndDate())
                .status(treatment.getStatus())
                .veterinarianName(treatment.getVeterinarianName())
                .notes(treatment.getNotes())
                .cost(treatment.getCost())
                .createdAt(treatment.getCreatedAt())
                .updatedAt(treatment.getUpdatedAt())
                .build();
    }

    public static List<TreatmentResponse> toResponseList(List<Treatment> treatments) {
        return treatments.stream()
                .map(TreatmentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
