package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.response.DashboardStatisticsResponse;
import com.hss.hss_backend.dto.response.FinancialStatisticsResponse;
import com.hss.hss_backend.dto.response.MedicalStatisticsResponse;
import com.hss.hss_backend.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Statistics", description = "Statistics and analytics APIs")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<DashboardStatisticsResponse> getDashboardStatistics() {
        log.info("Fetching dashboard statistics");
        DashboardStatisticsResponse statistics = statisticsService.getDashboardStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/financial")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Get financial statistics")
    public ResponseEntity<FinancialStatisticsResponse> getFinancialStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        log.info("Fetching financial statistics from {} to {}", startDate, endDate);
        FinancialStatisticsResponse statistics = statisticsService.getFinancialStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/medical")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Get medical statistics")
    public ResponseEntity<MedicalStatisticsResponse> getMedicalStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        log.info("Fetching medical statistics from {} to {}", startDate, endDate);
        MedicalStatisticsResponse statistics = statisticsService.getMedicalStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
}

