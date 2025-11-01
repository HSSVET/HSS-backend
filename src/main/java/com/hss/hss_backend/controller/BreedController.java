package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.BreedCreateRequest;
import com.hss.hss_backend.dto.request.BreedUpdateRequest;
import com.hss.hss_backend.dto.response.BreedResponse;
import com.hss.hss_backend.service.BreedService;
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
@RequestMapping("/api/breeds")
@RequiredArgsConstructor
@Slf4j
public class BreedController {

    private final BreedService breedService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public ResponseEntity<BreedResponse> createBreed(@Valid @RequestBody BreedCreateRequest request) {
        log.info("Creating breed: {} for species ID: {}", request.getName(), request.getSpeciesId());
        BreedResponse response = breedService.createBreed(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<BreedResponse> getBreedById(@PathVariable Long id) {
        log.info("Fetching breed with ID: {}", id);
        BreedResponse response = breedService.getBreedById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<Page<BreedResponse>> getAllBreeds(Pageable pageable) {
        log.info("Fetching all breeds with pagination");
        Page<BreedResponse> response = breedService.getAllBreeds(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/species/{speciesId}")
    public ResponseEntity<List<BreedResponse>> getBreedsBySpeciesId(@PathVariable Long speciesId) {
        log.info("Fetching breeds for species ID: {}", speciesId);
        List<BreedResponse> response = breedService.getBreedsBySpeciesId(speciesId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<BreedResponse>> searchBreedsByName(@RequestParam String name) {
        log.info("Searching breeds by name: {}", name);
        List<BreedResponse> response = breedService.searchBreedsByName(name);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public ResponseEntity<BreedResponse> updateBreed(@PathVariable Long id, @Valid @RequestBody BreedUpdateRequest request) {
        log.info("Updating breed with ID: {}", id);
        BreedResponse response = breedService.updateBreed(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBreed(@PathVariable Long id) {
        log.info("Deleting breed with ID: {}", id);
        breedService.deleteBreed(id);
        return ResponseEntity.noContent().build();
    }
}
