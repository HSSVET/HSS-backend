package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.RadiologicalImagingCreateRequest;
import com.hss.hss_backend.dto.response.RadiologicalImagingResponse;
import com.hss.hss_backend.service.RadiologicalImagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/animals/{animalId}/radiology")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Radiological Imaging Management", description = "APIs for managing radiological imagings")
public class RadiologicalImagingController {

    private final RadiologicalImagingService radiologicalImagingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Create a new radiological imaging for an animal with image upload")
    public ResponseEntity<RadiologicalImagingResponse> createRadiologicalImaging(
            @PathVariable Long animalId,
            @RequestParam("type") String type,
            @RequestParam("date") String dateStr,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        log.info("Creating radiological imaging for animal ID: {}", animalId);

        RadiologicalImagingCreateRequest request = RadiologicalImagingCreateRequest.builder()
                .animalId(animalId)
                .type(type)
                .date(LocalDate.parse(dateStr))
                .comment(comment)
                .build();

        RadiologicalImagingResponse response = radiologicalImagingService.createRadiologicalImaging(request, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get all radiological imagings for an animal")
    public ResponseEntity<List<RadiologicalImagingResponse>> getRadiologicalImagingsByAnimalId(@PathVariable Long animalId) {
        log.info("Fetching radiological imagings for animal ID: {}", animalId);
        List<RadiologicalImagingResponse> response = radiologicalImagingService.getRadiologicalImagingsByAnimalId(animalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get radiological imaging by ID")
    public ResponseEntity<RadiologicalImagingResponse> getRadiologicalImagingById(
            @PathVariable Long animalId,
            @PathVariable Long imageId) {
        log.info("Fetching radiological imaging with ID: {}", imageId);
        RadiologicalImagingResponse response = radiologicalImagingService.getRadiologicalImagingById(imageId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Delete a radiological imaging")
    public ResponseEntity<Void> deleteRadiologicalImaging(
            @PathVariable Long animalId,
            @PathVariable Long imageId) {
        log.info("Deleting radiological imaging with ID: {}", imageId);
        radiologicalImagingService.deleteRadiologicalImaging(imageId);
        return ResponseEntity.noContent().build();
    }
}
