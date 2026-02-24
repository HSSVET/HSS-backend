package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.request.ClinicalExaminationCreateRequest;
import com.hss.hss_backend.dto.response.ClinicalExaminationResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.ClinicalExamination;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClinicalExaminationMapper {

    public static ClinicalExamination toEntity(ClinicalExaminationCreateRequest request, Animal animal) {
        return ClinicalExamination.builder()
                .animal(animal)
                .date(request.getDate())
                .findings(request.getFindings())
                .veterinarianName(request.getVeterinarianName())
                .build();
    }

    public static ClinicalExaminationResponse toResponse(ClinicalExamination examination) {
        return ClinicalExaminationResponse.builder()
                .examinationId(examination.getExaminationId())
                .animalId(examination.getAnimal().getAnimalId())
                .animalName(examination.getAnimal().getName())
                .date(examination.getDate())
                .findings(examination.getFindings())
                .veterinarianName(examination.getVeterinarianName())
                .createdAt(examination.getCreatedAt())
                .updatedAt(examination.getUpdatedAt())
                .build();
    }

    public static List<ClinicalExaminationResponse> toResponseList(List<ClinicalExamination> examinations) {
        return examinations.stream()
                .map(ClinicalExaminationMapper::toResponse)
                .collect(Collectors.toList());
    }
}
