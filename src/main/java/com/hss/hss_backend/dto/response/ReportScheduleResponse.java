package com.hss.hss_backend.dto.response;

import com.hss.hss_backend.entity.ReportSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportScheduleResponse {

    private Long reportId;
    private String name;
    private String description;
    private ReportSchedule.Frequency frequency;
    private String cronExpression;
    private LocalDateTime lastRun;
    private LocalDateTime nextRun;
    private Boolean isActive;
    private ReportSchedule.ReportType reportType;
    private String parameters;
    private List<String> emailRecipients;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

