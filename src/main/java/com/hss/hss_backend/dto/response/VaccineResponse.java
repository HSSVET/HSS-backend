package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineResponse {
    
    private Long id;
    private String name;
    private String administrationRoute;
    private Long protectionPeriodDays;
    private Integer ageRequirementMonths;
    private Boolean boosterRequired;
    private String notes;
    private String manufacturer;
}
