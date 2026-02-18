package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.TreatmentCreateRequest;
import com.hss.hss_backend.dto.request.TreatmentUpdateRequest;
import com.hss.hss_backend.dto.response.TreatmentResponse;
import com.hss.hss_backend.service.TreatmentService;
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
@RequestMapping("/api/animals/{animalId}/treatments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Treatment Management", description = "APIs for managing animal treatments")
public class TreatmentController {

    private final TreatmentService treatmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Create a new treatment for an animal")
    public ResponseEntity<TreatmentResponse> createTreatment(
            @PathVariable Long animalId,
            @Valid @RequestBody TreatmentCreateRequest request) {
        log.info("Creating treatment for animal ID: {}", animalId);
        request.setAnimalId(animalId);
        TreatmentResponse response = treatmentService.createTreatment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get all treatments for an animal")
    public ResponseEntity<List<TreatmentResponse>> getTreatmentsByAnimalId(@PathVariable Long animalId) {
        log.info("Fetching treatments for animal ID: {}", animalId);
        List<TreatmentResponse> response = treatmentService.getTreatmentsByAnimalId(animalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{treatmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get treatment by ID")
    public ResponseEntity<TreatmentResponse> getTreatmentById(
            @PathVariable Long animalId,
            @PathVariable Long treatmentId) {
        log.info("Fetching treatment with ID: {}", treatmentId);
        TreatmentResponse response = treatmentService.getTreatmentById(treatmentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{treatmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Update a treatment")
    public ResponseEntity<TreatmentResponse> updateTreatment(
            @PathVariable Long animalId,
            @PathVariable Long treatmentId,
            @Valid @RequestBody TreatmentUpdateRequest request) {
        log.info("Updating treatment with ID: {}", treatmentId);
        TreatmentResponse response = treatmentService.updateTreatment(treatmentId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{treatmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Delete a treatment")
    public ResponseEntity<Void> deleteTreatment(
            @PathVariable Long animalId,
            @PathVariable Long treatmentId) {
        log.info("Deleting treatment with ID: {}", treatmentId);
        treatmentService.deleteTreatment(treatmentId);
        return ResponseEntity.noContent().build();
    }
}
