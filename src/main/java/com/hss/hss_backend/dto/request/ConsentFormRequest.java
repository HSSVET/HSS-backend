package com.hss.hss_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for recording consent form signature.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentFormRequest {

    private String formType; // ANESTHESIA, SURGERY, TREATMENT, GENERAL
    private String signatureData; // Base64 encoded signature image
    private String signerName;
    private String signerRelation;
    private String witnessName;
    private String notes;
}
