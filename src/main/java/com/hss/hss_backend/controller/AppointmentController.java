package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.AppointmentCreateRequest;
import com.hss.hss_backend.dto.request.AppointmentUpdateRequest;
import com.hss.hss_backend.dto.response.AppointmentResponse;
import com.hss.hss_backend.entity.Appointment;
import com.hss.hss_backend.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST') or hasRole('OWNER')")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        log.info("Fetching appointment with ID: {}", id);
        AppointmentResponse response = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointments(Pageable pageable) {
        log.info("Fetching all appointments with pagination");
        Page<AppointmentResponse> response = appointmentService.getAllAppointments(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST') or hasRole('OWNER')")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByAnimalId(@PathVariable Long animalId) {
        log.info("Fetching appointments for animal ID: {}", animalId);
        List<AppointmentResponse> response = appointmentService.getAppointmentsByAnimalId(animalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/veterinarian/{veterinarianId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByVeterinarianId(
            @PathVariable Long veterinarianId) {
        log.info("Fetching appointments for veterinarian ID: {}", veterinarianId);
        List<AppointmentResponse> response = appointmentService.getAppointmentsByVeterinarianId(veterinarianId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDateRange(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam(name = "startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTimeAlias,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestParam(name = "endDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTimeAlias) {

        LocalDateTime finalStart = startDateTime != null ? startDateTime : startDateTimeAlias;
        LocalDateTime finalEnd = endDateTime != null ? endDateTime : endDateTimeAlias;

        if (finalStart == null || finalEnd == null) {
            // fallback logic or throw exception, but let's assume one pair is present.
            // Actually, cleaner is just to rename the params to what frontend sends, or
            // support both via alias.
            throw new IllegalArgumentException("StartDate and EndDate are required");
        }

        log.info("Fetching appointments between {} and {}", finalStart, finalEnd);
        List<AppointmentResponse> response = appointmentService.getAppointmentsByDateRange(finalStart, finalEnd);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/animal/{animalId}/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST') or hasRole('OWNER')")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByAnimalAndDateRange(
            @PathVariable Long animalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        log.info("Fetching appointments for animal {} between {} and {}", animalId, startDateTime, endDateTime);
        List<AppointmentResponse> response = appointmentService.getAppointmentsByAnimalAndDateRange(animalId,
                startDateTime, endDateTime);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDateTime) {
        log.info("Fetching upcoming appointments from {}", fromDateTime);
        List<AppointmentResponse> response = appointmentService.getUpcomingAppointments(fromDateTime);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByStatus(@PathVariable Appointment.Status status) {
        log.info("Fetching appointments with status: {}", status);
        List<AppointmentResponse> response = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<AppointmentResponse>> getTodayAppointments() {
        log.info("Fetching today's appointments");
        List<AppointmentResponse> response = appointmentService.getTodayAppointments();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/calendar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<AppointmentResponse>> getCalendarAppointments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching calendar appointments between {} and {}", startDate, endDate);
        List<AppointmentResponse> response = appointmentService.getAppointmentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST') or hasRole('OWNER')")
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentCreateRequest request) {
        log.info("Creating appointment for animal ID: {} at {}", request.getAnimalId(), request.getDateTime());
        AppointmentResponse response = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam Appointment.Status status) {
        log.info("Updating appointment {} status to {}", id, status);
        AppointmentResponse response = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateRequest request) {
        log.info("Updating appointment with ID: {}", id);
        AppointmentResponse response = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        log.info("Deleting appointment with ID: {}", id);
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
