package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.response.VaccinationScheduleResponse;
import com.hss.hss_backend.service.VaccinationScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccination-schedules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vaccination Schedule", description = "Vaccination schedule management APIs")
public class VaccinationScheduleController {

    private final VaccinationScheduleService scheduleService;

    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get vaccination schedules by animal ID")
    public ResponseEntity<List<VaccinationScheduleResponse>> getSchedulesByAnimalId(@PathVariable Long animalId) {
        log.info("Fetching vaccination schedules for animal ID: {}", animalId);
        List<VaccinationScheduleResponse> schedules = scheduleService.getSchedulesByAnimalId(animalId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get all pending vaccination schedules")
    public ResponseEntity<List<VaccinationScheduleResponse>> getPendingSchedules() {
        log.info("Fetching pending vaccination schedules");
        List<VaccinationScheduleResponse> schedules = scheduleService.getPendingSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get all overdue vaccination schedules")
    public ResponseEntity<List<VaccinationScheduleResponse>> getOverdueSchedules() {
        log.info("Fetching overdue vaccination schedules");
        List<VaccinationScheduleResponse> schedules = scheduleService.getOverdueSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get upcoming vaccination schedules")
    public ResponseEntity<List<VaccinationScheduleResponse>> getUpcomingSchedules(
            @RequestParam(defaultValue = "30") int days) {
        log.info("Fetching upcoming vaccination schedules for next {} days", days);
        List<VaccinationScheduleResponse> schedules = scheduleService.getUpcomingSchedules(days);
        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/animal/{animalId}/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Generate vaccination schedule for an animal")
    public ResponseEntity<Void> generateScheduleForAnimal(@PathVariable Long animalId) {
        log.info("Generating vaccination schedule for animal ID: {}", animalId);
        scheduleService.generateScheduleForAnimal(animalId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{scheduleId}/complete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Mark a vaccination schedule as completed")
    public ResponseEntity<Void> markScheduleAsCompleted(
            @PathVariable Long scheduleId,
            @RequestParam(required = false) Long vaccinationRecordId) {
        log.info("Marking schedule {} as completed with record {}", scheduleId, vaccinationRecordId);
        scheduleService.markScheduleAsCompleted(scheduleId, vaccinationRecordId);
        return ResponseEntity.ok().build();
    }
}

