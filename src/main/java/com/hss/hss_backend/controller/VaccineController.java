package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.VaccineCreateRequest;
import com.hss.hss_backend.dto.request.VaccineUpdateRequest;
import com.hss.hss_backend.dto.response.VaccineResponse;
import com.hss.hss_backend.service.VaccineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccines")
@RequiredArgsConstructor
@Slf4j
public class VaccineController {
    
    private final VaccineService vaccineService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public ResponseEntity<VaccineResponse> createVaccine(@Valid @RequestBody VaccineCreateRequest request) {
        log.info("Creating vaccine: {}", request.getVaccineName());
        VaccineResponse response = vaccineService.createVaccine(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<VaccineResponse> getVaccineById(@PathVariable Long id) {
        log.info("Fetching vaccine with ID: {}", id);
        VaccineResponse response = vaccineService.getVaccineById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<List<VaccineResponse>> getAllVaccines() {
        log.info("Fetching all vaccines");
        List<VaccineResponse> response = vaccineService.getAllVaccines();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<List<VaccineResponse>> searchVaccines(@RequestParam String name) {
        log.info("Searching vaccines by name: {}", name);
        List<VaccineResponse> response = vaccineService.searchVaccinesByName(name);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public ResponseEntity<VaccineResponse> updateVaccine(@PathVariable Long id,
            @Valid @RequestBody VaccineUpdateRequest request) {
        log.info("Updating vaccine with ID: {}", id);
        VaccineResponse response = vaccineService.updateVaccine(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVaccine(@PathVariable Long id) {
        log.info("Deleting vaccine with ID: {}", id);
        vaccineService.deleteVaccine(id);
        return ResponseEntity.noContent().build();
    }
}
