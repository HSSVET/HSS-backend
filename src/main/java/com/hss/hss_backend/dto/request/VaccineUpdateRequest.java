package com.hss.hss_backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineUpdateRequest {
    
    @Size(max = 100, message = "Vaccine name must not exceed 100 characters")
    private String vaccineName;
    
    @Size(max = 50, message = "Administration route must not exceed 50 characters")
    private String administrationRoute;
    
    private Long protectionPeriodDays;
    
    private Integer ageRequirementMonths;
    
    private Boolean boosterRequired;
    
    private String notes;
    
    private String manufacturer;
}
