package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabStatsDto {
  private long total;
  private long pending;
  private long inProgress;
  private long completed;
  private long cancelled;
  private long today;
}
