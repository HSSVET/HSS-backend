package com.hss.hss_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for scanning a barcode to find stock product information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarcodeScanRequest {

    @NotBlank(message = "Barcode is required")
    private String barcode;
}
