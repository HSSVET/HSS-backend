package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.SurgeryDto;
import com.hss.hss_backend.dto.SurgeryMedicationDto;
import com.hss.hss_backend.service.SurgeryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surgeries")
@RequiredArgsConstructor
@Tag(name = "Surgery Management", description = "APIs for managing surgeries")
public class SurgeryController {

  private final SurgeryService surgeryService;

  @PostMapping
  @Operation(summary = "Create a planned surgery")
  public ResponseEntity<SurgeryDto> createSurgery(@RequestBody SurgeryDto surgeryDto) {
    return ResponseEntity.ok(surgeryService.createSurgery(surgeryDto));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get surgery details by ID")
  public ResponseEntity<SurgeryDto> getSurgeryById(@PathVariable Long id) {
    return ResponseEntity.ok(surgeryService.getSurgeryById(id));
  }

  @GetMapping("/animal/{animalId}")
  @Operation(summary = "Get surgeries for a specific animal")
  public ResponseEntity<List<SurgeryDto>> getSurgeriesByAnimal(@PathVariable Long animalId) {
    return ResponseEntity.ok(surgeryService.getSurgeriesByAnimalId(animalId));
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Update surgery status")
  public ResponseEntity<SurgeryDto> updateStatus(@PathVariable Long id, @RequestParam String status) {
    return ResponseEntity.ok(surgeryService.updateSurgeryStatus(id, status));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update surgery details")
  public ResponseEntity<SurgeryDto> updateDetails(@PathVariable Long id, @RequestBody SurgeryDto surgeryDto) {
    return ResponseEntity.ok(surgeryService.updateSurgeryDetails(id, surgeryDto));
  }

  @PostMapping("/{id}/medications")
  @Operation(summary = "Add medication used in surgery")
  public ResponseEntity<SurgeryDto> addMedication(@PathVariable Long id,
      @RequestBody SurgeryMedicationDto medicationDto) {
    return ResponseEntity.ok(surgeryService.addMedication(id, medicationDto));
  }
}
