package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.AnimalCreateRequest;
import com.hss.hss_backend.dto.request.AnimalUpdateRequest;
import com.hss.hss_backend.dto.response.AnimalDetailResponse;
import com.hss.hss_backend.dto.response.AnimalResponse;
import com.hss.hss_backend.service.AnimalService;
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
@RequestMapping("/api/animals")
@RequiredArgsConstructor
@Slf4j
public class AnimalController {

    private final AnimalService animalService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<AnimalResponse> createAnimal(@Valid @RequestBody AnimalCreateRequest request) {
        log.info("Creating animal: {}", request.getName());
        AnimalResponse response = animalService.createAnimal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST') or hasRole('OWNER')")
    public ResponseEntity<AnimalResponse> getAnimalById(@PathVariable Long id) {
        log.info("Fetching animal with ID: {}", id);
        AnimalResponse response = animalService.getAnimalById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/detail")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('OWNER')")
    public ResponseEntity<AnimalDetailResponse> getAnimalDetailById(@PathVariable Long id) {
        log.info("Fetching animal detail with ID: {}", id);
        AnimalDetailResponse response = animalService.getAnimalDetailById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<Page<AnimalResponse>> getAllAnimals(
            Pageable pageable,
            @RequestParam(required = false) String status) {
        log.info("Fetching all animals with pagination, status filter: {}", status);
        Page<AnimalResponse> response = animalService.getAllAnimals(pageable, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST') or hasRole('OWNER')")
    public ResponseEntity<List<AnimalResponse>> getAnimalsByOwnerId(@PathVariable Long ownerId) {
        log.info("Fetching animals for owner ID: {}", ownerId);
        List<AnimalResponse> response = animalService.getAnimalsByOwnerId(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<AnimalResponse>> searchAnimalsByName(@RequestParam String name) {
        log.info("Searching animals by name: {}", name);
        List<AnimalResponse> response = animalService.searchAnimalsByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/owner")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<AnimalResponse>> searchAnimalsByOwnerName(@RequestParam String ownerName) {
        log.info("Searching animals by owner name: {}", ownerName);
        List<AnimalResponse> response = animalService.searchAnimalsByOwnerName(ownerName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<AnimalResponse>> searchAnimals(@RequestParam String query) {
        log.info("Searching animals with query: {}", query);
        List<AnimalResponse> response = animalService.searchAnimals(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/microchip/{microchipNo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<AnimalResponse> getAnimalByMicrochip(@PathVariable String microchipNo) {
        log.info("Fetching animal by microchip: {}", microchipNo);
        AnimalResponse response = animalService.getAnimalByMicrochip(microchipNo);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    public ResponseEntity<AnimalResponse> updateAnimal(@PathVariable Long id,
            @Valid @RequestBody AnimalUpdateRequest request) {
        log.info("Updating animal with ID: {}", id);
        AnimalResponse response = animalService.updateAnimal(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        log.info("Deleting animal with ID: {}", id);
        animalService.deleteAnimal(id);
        return ResponseEntity.noContent().build();
    }
}
