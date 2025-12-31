package com.hss.hss_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDataResponse {

    private String reportId;
    private String reportName;
    private String reportType;
    private LocalDateTime generatedAt;
    private Map<String, Object> data;
    private String format; // PDF, EXCEL, JSON
    private String downloadUrl;
}

