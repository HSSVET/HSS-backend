package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.VaccineCreateRequest;
import com.hss.hss_backend.dto.request.VaccineUpdateRequest;
import com.hss.hss_backend.dto.response.VaccineResponse;
import com.hss.hss_backend.service.VaccineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccines")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vaccine Management", description = "APIs for managing vaccine inventory and types")
public class VaccineController {

  private final VaccineService vaccineService;

  @PostMapping
  @Operation(summary = "Create a new vaccine")
  public ResponseEntity<VaccineResponse> createVaccine(@RequestBody VaccineCreateRequest request) {
    log.info("Creating vaccine: {}", request.getVaccineName());
    return new ResponseEntity<>(vaccineService.createVaccine(request), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get vaccine by ID")
  public ResponseEntity<VaccineResponse> getVaccineById(@PathVariable Long id) {
    log.info("Fetching vaccine with ID: {}", id);
    return ResponseEntity.ok(vaccineService.getVaccineById(id));
  }

  @GetMapping
  @Operation(summary = "Get all vaccines")
  public ResponseEntity<List<VaccineResponse>> getAllVaccines() {
    log.info("Fetching all vaccines");
    return ResponseEntity.ok(vaccineService.getAllVaccines());
  }

  @GetMapping("/paged")
  @Operation(summary = "Get all vaccines with pagination")
  public ResponseEntity<Page<VaccineResponse>> getAllVaccinesPaged(Pageable pageable) {
    log.info("Fetching all vaccines with pagination");
    return ResponseEntity.ok(vaccineService.getAllVaccinesPaged(pageable));
  }

  @GetMapping("/search")
  @Operation(summary = "Search vaccines by name")
  public ResponseEntity<List<VaccineResponse>> searchVaccinesByName(@RequestParam String name) {
    log.info("Searching vaccines by name: {}", name);
    return ResponseEntity.ok(vaccineService.searchVaccinesByName(name));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update an existing vaccine")
  public ResponseEntity<VaccineResponse> updateVaccine(@PathVariable Long id,
      @RequestBody VaccineUpdateRequest request) {
    log.info("Updating vaccine with ID: {}", id);
    return ResponseEntity.ok(vaccineService.updateVaccine(id, request));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a vaccine")
  public ResponseEntity<Void> deleteVaccine(@PathVariable Long id) {
    log.info("Deleting vaccine with ID: {}", id);
    vaccineService.deleteVaccine(id);
    return ResponseEntity.noContent().build();
  }
}
