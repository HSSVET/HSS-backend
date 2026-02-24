package com.hss.hss_backend.dto.request;

import com.hss.hss_backend.entity.BehaviorNote;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorNoteCreateRequest {

    @NotNull(message = "Animal ID is required")
    private Long animalId;

    private BehaviorNote.BehaviorCategory category;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private BehaviorNote.Severity severity;

    @NotNull(message = "Observed date is required")
    private LocalDate observedDate;

    @Size(max = 100, message = "Observed by must not exceed 100 characters")
    private String observedBy;

    private String recommendations;
}
