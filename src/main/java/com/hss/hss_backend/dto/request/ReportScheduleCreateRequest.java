package com.hss.hss_backend.dto.request;

import com.hss.hss_backend.entity.ReportSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReportScheduleCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Frequency is required")
    private ReportSchedule.Frequency frequency;

    private String cronExpression;

    @NotNull(message = "Report type is required")
    private ReportSchedule.ReportType reportType;

    private String parameters; // JSON string
    private List<String> emailRecipients;
    private Boolean isActive;
}

