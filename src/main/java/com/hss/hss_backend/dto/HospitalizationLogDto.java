package com.hss.hss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalizationLogDto {
  private Long logId;
  private Long hospitalizationId;
  private LocalDateTime logDate;
  private String notes;
  private String vitalSigns; // JSON string
  private String entryBy;
}
