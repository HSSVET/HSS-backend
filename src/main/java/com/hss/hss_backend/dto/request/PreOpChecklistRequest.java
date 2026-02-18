package com.hss.hss_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating pre-operative checklist status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreOpChecklistRequest {

    private Boolean preOpExamCompleted;
    private Boolean preOpTestsCompleted;
    private Boolean fastingConfirmed;
    private String requiredTests; // JSON array of test types
    private Integer fastingHours;
    private String notes;
}
