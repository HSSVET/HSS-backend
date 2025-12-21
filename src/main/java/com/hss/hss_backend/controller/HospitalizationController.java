package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.HospitalizationDto;
import com.hss.hss_backend.dto.HospitalizationLogDto;
import com.hss.hss_backend.service.HospitalizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitalizations")
@RequiredArgsConstructor
@Tag(name = "Hospitalization Management", description = "APIs for managing hospitalizations")
public class HospitalizationController {

  private final HospitalizationService hospitalizationService;

  @PostMapping
  @Operation(summary = "Admit a patient")
  public ResponseEntity<HospitalizationDto> admitPatient(@RequestBody HospitalizationDto dto) {
    return ResponseEntity.ok(hospitalizationService.admitPatient(dto));
  }

  @PutMapping("/{id}/discharge")
  @Operation(summary = "Discharge a patient")
  public ResponseEntity<HospitalizationDto> dischargePatient(@PathVariable Long id) {
    return ResponseEntity.ok(hospitalizationService.dischargePatient(id));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get hospitalization details by ID")
  public ResponseEntity<HospitalizationDto> getHospitalizationById(@PathVariable Long id) {
    return ResponseEntity.ok(hospitalizationService.getHospitalizationById(id));
  }

  @GetMapping("/active")
  @Operation(summary = "Get all active hospitalizations")
  public ResponseEntity<List<HospitalizationDto>> getActiveHospitalizations() {
    return ResponseEntity.ok(hospitalizationService.getActiveHospitalizations());
  }

  @PostMapping("/{id}/logs")
  @Operation(summary = "Add a daily log/vital sign entry")
  public ResponseEntity<HospitalizationLogDto> addLog(@PathVariable Long id,
      @RequestBody HospitalizationLogDto logDto) {
    return ResponseEntity.ok(hospitalizationService.addLog(id, logDto));
  }
}
