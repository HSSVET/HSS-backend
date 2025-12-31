package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.ReportScheduleCreateRequest;
import com.hss.hss_backend.dto.request.ReportScheduleUpdateRequest;
import com.hss.hss_backend.dto.response.ReportDataResponse;
import com.hss.hss_backend.dto.response.ReportScheduleResponse;
import com.hss.hss_backend.service.ReportScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report-schedules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report Schedule", description = "Report schedule management APIs")
public class ReportScheduleController {

    private final ReportScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Create a new report schedule")
    public ResponseEntity<ReportScheduleResponse> createSchedule(
            @Valid @RequestBody ReportScheduleCreateRequest request) {
        log.info("Creating report schedule: {}", request.getName());
        ReportScheduleResponse response = scheduleService.createSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get all report schedules")
    public ResponseEntity<List<ReportScheduleResponse>> getAllSchedules() {
        log.info("Fetching all report schedules");
        List<ReportScheduleResponse> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get all active report schedules")
    public ResponseEntity<List<ReportScheduleResponse>> getActiveSchedules() {
        log.info("Fetching active report schedules");
        List<ReportScheduleResponse> schedules = scheduleService.getActiveSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get report schedule by ID")
    public ResponseEntity<ReportScheduleResponse> getScheduleById(@PathVariable Long id) {
        log.info("Fetching report schedule with ID: {}", id);
        ReportScheduleResponse schedule = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(schedule);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Update a report schedule")
    public ResponseEntity<ReportScheduleResponse> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ReportScheduleUpdateRequest request) {
        log.info("Updating report schedule with ID: {}", id);
        ReportScheduleResponse response = scheduleService.updateSchedule(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a report schedule")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        log.info("Deleting report schedule with ID: {}", id);
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/execute")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Manually execute a report schedule")
    public ResponseEntity<ReportDataResponse> executeSchedule(@PathVariable Long id) {
        log.info("Manually executing report schedule ID: {}", id);
        ReportDataResponse response = scheduleService.executeSchedule(id);
        return ResponseEntity.ok(response);
    }
}

