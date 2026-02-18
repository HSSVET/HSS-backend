package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.request.BehaviorNoteCreateRequest;
import com.hss.hss_backend.dto.request.BehaviorNoteUpdateRequest;
import com.hss.hss_backend.dto.response.BehaviorNoteResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.BehaviorNote;
import com.hss.hss_backend.entity.Clinic;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BehaviorNoteMapper {

    public static BehaviorNote toEntity(BehaviorNoteCreateRequest request, Animal animal, Clinic clinic) {
        return BehaviorNote.builder()
                .animal(animal)
                .clinic(clinic)
                .category(request.getCategory())
                .title(request.getTitle())
                .description(request.getDescription())
                .severity(request.getSeverity())
                .observedDate(request.getObservedDate())
                .observedBy(request.getObservedBy())
                .recommendations(request.getRecommendations())
                .build();
    }

    public static void updateEntity(BehaviorNote behaviorNote, BehaviorNoteUpdateRequest request) {
        if (request.getCategory() != null) {
            behaviorNote.setCategory(request.getCategory());
        }
        if (request.getTitle() != null) {
            behaviorNote.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            behaviorNote.setDescription(request.getDescription());
        }
        if (request.getSeverity() != null) {
            behaviorNote.setSeverity(request.getSeverity());
        }
        if (request.getObservedDate() != null) {
            behaviorNote.setObservedDate(request.getObservedDate());
        }
        if (request.getObservedBy() != null) {
            behaviorNote.setObservedBy(request.getObservedBy());
        }
        if (request.getRecommendations() != null) {
            behaviorNote.setRecommendations(request.getRecommendations());
        }
    }

    public static BehaviorNoteResponse toResponse(BehaviorNote behaviorNote) {
        return BehaviorNoteResponse.builder()
                .behaviorNoteId(behaviorNote.getBehaviorNoteId())
                .animalId(behaviorNote.getAnimal().getAnimalId())
                .animalName(behaviorNote.getAnimal().getName())
                .clinicId(behaviorNote.getClinic().getClinicId())
                .category(behaviorNote.getCategory())
                .title(behaviorNote.getTitle())
                .description(behaviorNote.getDescription())
                .severity(behaviorNote.getSeverity())
                .observedDate(behaviorNote.getObservedDate())
                .observedBy(behaviorNote.getObservedBy())
                .recommendations(behaviorNote.getRecommendations())
                .createdAt(behaviorNote.getCreatedAt())
                .updatedAt(behaviorNote.getUpdatedAt())
                .build();
    }

    public static List<BehaviorNoteResponse> toResponseList(List<BehaviorNote> behaviorNotes) {
        return behaviorNotes.stream()
                .map(BehaviorNoteMapper::toResponse)
                .collect(Collectors.toList());
    }
}
