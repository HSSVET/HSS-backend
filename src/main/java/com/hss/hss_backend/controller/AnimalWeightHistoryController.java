package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.AnimalWeightHistoryRequest;
import com.hss.hss_backend.dto.response.AnimalWeightHistoryResponse;
import com.hss.hss_backend.service.AnimalWeightHistoryService;
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
@RequestMapping("/api/animals/{animalId}/weight-history")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Animal Weight History Management", description = "APIs for managing animal weight history")
public class AnimalWeightHistoryController {

    private final AnimalWeightHistoryService animalWeightHistoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Add a new weight record for an animal")
    public ResponseEntity<AnimalWeightHistoryResponse> addWeightRecord(
            @PathVariable Long animalId,
            @Valid @RequestBody AnimalWeightHistoryRequest request) {
        log.info("Adding weight record for animal ID: {}", animalId);
        AnimalWeightHistoryResponse response = animalWeightHistoryService.addWeightRecord(animalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get weight history for an animal")
    public ResponseEntity<List<AnimalWeightHistoryResponse>> getWeightHistory(@PathVariable Long animalId) {
        log.info("Fetching weight history for animal ID: {}", animalId);
        List<AnimalWeightHistoryResponse> response = animalWeightHistoryService.getWeightHistory(animalId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{weightId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Update a weight record")
    public ResponseEntity<AnimalWeightHistoryResponse> updateWeightRecord(
            @PathVariable Long animalId,
            @PathVariable Long weightId,
            @Valid @RequestBody AnimalWeightHistoryRequest request) {
        log.info("Updating weight record with ID: {}", weightId);
        AnimalWeightHistoryResponse response = animalWeightHistoryService.updateWeightRecord(weightId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{weightId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Delete a weight record")
    public ResponseEntity<Void> deleteWeightRecord(
            @PathVariable Long animalId,
            @PathVariable Long weightId) {
        log.info("Deleting weight record with ID: {}", weightId);
        animalWeightHistoryService.deleteWeightRecord(weightId);
        return ResponseEntity.noContent().build();
    }
}
