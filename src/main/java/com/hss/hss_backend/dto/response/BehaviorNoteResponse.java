package com.hss.hss_backend.dto.response;

import com.hss.hss_backend.entity.BehaviorNote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorNoteResponse {

    private Long behaviorNoteId;
    private Long animalId;
    private String animalName;
    private Long clinicId;
    private BehaviorNote.BehaviorCategory category;
    private String title;
    private String description;
    private BehaviorNote.Severity severity;
    private LocalDate observedDate;
    private String observedBy;
    private String recommendations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
