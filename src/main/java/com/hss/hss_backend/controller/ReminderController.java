package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.ReminderCreateRequest;
import com.hss.hss_backend.dto.response.ReminderResponse;
import com.hss.hss_backend.service.ReminderService;
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
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reminder", description = "Reminder management APIs")
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Create a new reminder")
    public ResponseEntity<ReminderResponse> createReminder(@Valid @RequestBody ReminderCreateRequest request) {
        log.info("Creating reminder for appointment ID: {}", request.getAppointmentId());
        ReminderResponse response = reminderService.createReminder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get reminders by appointment ID")
    public ResponseEntity<List<ReminderResponse>> getRemindersByAppointmentId(@PathVariable Long appointmentId) {
        log.info("Fetching reminders for appointment ID: {}", appointmentId);
        List<ReminderResponse> reminders = reminderService.getRemindersByAppointmentId(appointmentId);
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Get all pending reminders")
    public ResponseEntity<List<ReminderResponse>> getPendingReminders() {
        log.info("Fetching pending reminders");
        List<ReminderResponse> reminders = reminderService.getPendingReminders();
        return ResponseEntity.ok(reminders);
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Manually send a reminder")
    public ResponseEntity<Void> sendReminder(@PathVariable Long id) {
        log.info("Manually sending reminder ID: {}", id);
        reminderService.sendReminder(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Cancel a reminder")
    public ResponseEntity<Void> cancelReminder(@PathVariable Long id) {
        log.info("Cancelling reminder ID: {}", id);
        reminderService.cancelReminder(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/process")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Process pending reminders (Admin only)")
    public ResponseEntity<Void> processPendingReminders() {
        log.info("Processing pending reminders");
        reminderService.processPendingReminders();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/retry-failed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Retry failed reminders (Admin only)")
    public ResponseEntity<Void> retryFailedReminders() {
        log.info("Retrying failed reminders");
        reminderService.retryFailedReminders();
        return ResponseEntity.ok().build();
    }
}

