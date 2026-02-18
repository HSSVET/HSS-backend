package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.response.MedicalHistoryResponse;
import com.hss.hss_backend.service.MedicalHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animals/{animalId}/medical-history")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Medical History Management", description = "APIs for managing medical history")
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get medical history for an animal")
    public ResponseEntity<List<MedicalHistoryResponse>> getMedicalHistoriesByAnimalId(@PathVariable Long animalId) {
        log.info("Fetching medical history for animal ID: {}", animalId);
        List<MedicalHistoryResponse> response = medicalHistoryService.getMedicalHistoriesByAnimalId(animalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{historyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get medical history by ID")
    public ResponseEntity<MedicalHistoryResponse> getMedicalHistoryById(
            @PathVariable Long animalId,
            @PathVariable Long historyId) {
        log.info("Fetching medical history with ID: {}", historyId);
        MedicalHistoryResponse response = medicalHistoryService.getMedicalHistoryById(historyId);
        return ResponseEntity.ok(response);
    }
}
