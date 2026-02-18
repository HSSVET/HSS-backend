package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.ClinicalExaminationCreateRequest;
import com.hss.hss_backend.dto.response.ClinicalExaminationResponse;
import com.hss.hss_backend.service.ClinicalExaminationService;
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
@RequestMapping("/api/animals/{animalId}/examinations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Clinical Examination Management", description = "APIs for managing clinical examinations")
public class ClinicalExaminationController {

    private final ClinicalExaminationService clinicalExaminationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Create a new clinical examination for an animal")
    public ResponseEntity<ClinicalExaminationResponse> createClinicalExamination(
            @PathVariable Long animalId,
            @Valid @RequestBody ClinicalExaminationCreateRequest request) {
        log.info("Creating clinical examination for animal ID: {}", animalId);
        request.setAnimalId(animalId);
        ClinicalExaminationResponse response = clinicalExaminationService.createClinicalExamination(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get all clinical examinations for an animal")
    public ResponseEntity<List<ClinicalExaminationResponse>> getClinicalExaminationsByAnimalId(@PathVariable Long animalId) {
        log.info("Fetching clinical examinations for animal ID: {}", animalId);
        List<ClinicalExaminationResponse> response = clinicalExaminationService.getClinicalExaminationsByAnimalId(animalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{examinationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get clinical examination by ID")
    public ResponseEntity<ClinicalExaminationResponse> getClinicalExaminationById(
            @PathVariable Long animalId,
            @PathVariable Long examinationId) {
        log.info("Fetching clinical examination with ID: {}", examinationId);
        ClinicalExaminationResponse response = clinicalExaminationService.getClinicalExaminationById(examinationId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{examinationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Delete a clinical examination")
    public ResponseEntity<Void> deleteClinicalExamination(
            @PathVariable Long animalId,
            @PathVariable Long examinationId) {
        log.info("Deleting clinical examination with ID: {}", examinationId);
        clinicalExaminationService.deleteClinicalExamination(examinationId);
        return ResponseEntity.noContent().build();
    }
}
