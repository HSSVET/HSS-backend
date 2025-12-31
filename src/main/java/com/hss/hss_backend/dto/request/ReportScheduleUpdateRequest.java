package com.hss.hss_backend.dto.request;

import com.hss.hss_backend.entity.ReportSchedule;
import lombok.Data;

import java.util.List;

@Data
public class ReportScheduleUpdateRequest {

    private String name;
    private String description;
    private ReportSchedule.Frequency frequency;
    private String cronExpression;
    private ReportSchedule.ReportType reportType;
    private String parameters;
    private List<String> emailRecipients;
    private Boolean isActive;
}

