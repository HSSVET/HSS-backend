package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.SpeciesCreateRequest;
import com.hss.hss_backend.dto.request.SpeciesUpdateRequest;
import com.hss.hss_backend.dto.response.SpeciesResponse;
import com.hss.hss_backend.service.SpeciesService;
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
@RequestMapping("/api/species")
@RequiredArgsConstructor
@Slf4j
public class SpeciesController {

    private final SpeciesService speciesService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public ResponseEntity<SpeciesResponse> createSpecies(@Valid @RequestBody SpeciesCreateRequest request) {
        log.info("Creating species: {}", request.getName());
        SpeciesResponse response = speciesService.createSpecies(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<SpeciesResponse> getSpeciesById(@PathVariable Long id) {
        log.info("Fetching species with ID: {}", id);
        SpeciesResponse response = speciesService.getSpeciesById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<Page<SpeciesResponse>> getAllSpecies(Pageable pageable) {
        log.info("Fetching all species with pagination");
        Page<SpeciesResponse> response = speciesService.getAllSpecies(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SpeciesResponse>> getAllSpeciesList() {
        log.info("Fetching all species as list");
        List<SpeciesResponse> response = speciesService.getAllSpeciesList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<SpeciesResponse>> searchSpeciesByName(@RequestParam String name) {
        log.info("Searching species by name: {}", name);
        List<SpeciesResponse> response = speciesService.searchSpeciesByName(name);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public ResponseEntity<SpeciesResponse> updateSpecies(@PathVariable Long id, @Valid @RequestBody SpeciesUpdateRequest request) {
        log.info("Updating species with ID: {}", id);
        SpeciesResponse response = speciesService.updateSpecies(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSpecies(@PathVariable Long id) {
        log.info("Deleting species with ID: {}", id);
        speciesService.deleteSpecies(id);
        return ResponseEntity.noContent().build();
    }
}
