package com.hss.hss_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for completing a surgery.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryCompleteRequest {

    private String dischargeType; // SAME_DAY, HOSPITALIZATION, TRANSFER
    private String postOpNotes;
    private String complications;
    private Long followUpAppointmentId;
    private Long prescriptionId;
}
