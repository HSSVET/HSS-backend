package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.BarcodeScanRequest;
import com.hss.hss_backend.dto.request.VaccinationCreateRequest;
import com.hss.hss_backend.dto.response.BarcodeScanResponse;
import com.hss.hss_backend.entity.VaccinationRecord;
import com.hss.hss_backend.service.StockProductService;
import com.hss.hss_backend.service.VaccinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccinations")
@RequiredArgsConstructor
@Tag(name = "Vaccination Management", description = "APIs for managing vaccinations")
public class VaccinationController {

  private final VaccinationService vaccinationService;
  private final StockProductService stockProductService;

  @GetMapping
  @Operation(summary = "List vaccinations (optionally by animalId)")
  public ResponseEntity<List<VaccinationRecord>> getVaccinations(
      @RequestParam(value = "animalId", required = false) Long animalId
  ) {
    if (animalId != null) {
      return ResponseEntity.ok(vaccinationService.getVaccinationsByAnimalId(animalId));
    }
    // For now, return empty list when no filter is provided to keep API simple
    return ResponseEntity.ok(List.of());
  }

  @PostMapping
  @Operation(summary = "Record a new vaccination")
  public ResponseEntity<VaccinationRecord> createVaccination(@RequestBody VaccinationCreateRequest request) {
    return ResponseEntity.ok(vaccinationService.createVaccination(request));
  }

  @GetMapping("/animal/{animalId}")
  @Operation(summary = "Get vaccinations for a specific animal")
  public ResponseEntity<List<VaccinationRecord>> getVaccinationsByAnimal(@PathVariable Long animalId) {
    return ResponseEntity.ok(vaccinationService.getVaccinationsByAnimalId(animalId));
  }

  @GetMapping("/statistics")
  public ResponseEntity<java.util.Map<String, Object>> getVaccinationStats() {
    java.util.Map<String, Object> stats = new java.util.HashMap<>();
    // Mock stats for now or implement service logic
    stats.put("totalVaccines", 100);
    stats.put("totalAnimalsVaccinated", 50);
    stats.put("upcomingVaccinations", 5);
    stats.put("overdueVaccinations", 2);
    return ResponseEntity.ok(stats);
  }

  @PostMapping("/scan-barcode")
  @Operation(summary = "Scan a barcode to get stock product information for vaccination")
  public ResponseEntity<BarcodeScanResponse> scanBarcode(@Valid @RequestBody BarcodeScanRequest request) {
    BarcodeScanResponse response = stockProductService.scanBarcode(request.getBarcode());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/scan-barcode/{barcode}")
  @Operation(summary = "Scan a barcode via GET request")
  public ResponseEntity<BarcodeScanResponse> scanBarcodeGet(@PathVariable String barcode) {
    BarcodeScanResponse response = stockProductService.scanBarcode(barcode);
    return ResponseEntity.ok(response);
  }
}
