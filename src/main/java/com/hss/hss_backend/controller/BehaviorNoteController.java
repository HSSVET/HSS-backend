package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.BehaviorNoteCreateRequest;
import com.hss.hss_backend.dto.request.BehaviorNoteUpdateRequest;
import com.hss.hss_backend.dto.response.BehaviorNoteResponse;
import com.hss.hss_backend.service.BehaviorNoteService;
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
@RequestMapping("/api/animals/{animalId}/behavior-notes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Behavior Note Management", description = "APIs for managing animal behavior notes")
public class BehaviorNoteController {

    private final BehaviorNoteService behaviorNoteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Create a new behavior note for an animal")
    public ResponseEntity<BehaviorNoteResponse> createBehaviorNote(
            @PathVariable Long animalId,
            @Valid @RequestBody BehaviorNoteCreateRequest request) {
        log.info("Creating behavior note for animal ID: {}", animalId);
        request.setAnimalId(animalId);
        BehaviorNoteResponse response = behaviorNoteService.createBehaviorNote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get all behavior notes for an animal")
    public ResponseEntity<List<BehaviorNoteResponse>> getBehaviorNotesByAnimalId(@PathVariable Long animalId) {
        log.info("Fetching behavior notes for animal ID: {}", animalId);
        List<BehaviorNoteResponse> response = behaviorNoteService.getBehaviorNotesByAnimalId(animalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{noteId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    @Operation(summary = "Get behavior note by ID")
    public ResponseEntity<BehaviorNoteResponse> getBehaviorNoteById(
            @PathVariable Long animalId,
            @PathVariable Long noteId) {
        log.info("Fetching behavior note with ID: {}", noteId);
        BehaviorNoteResponse response = behaviorNoteService.getBehaviorNoteById(noteId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{noteId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF')")
    @Operation(summary = "Update a behavior note")
    public ResponseEntity<BehaviorNoteResponse> updateBehaviorNote(
            @PathVariable Long animalId,
            @PathVariable Long noteId,
            @Valid @RequestBody BehaviorNoteUpdateRequest request) {
        log.info("Updating behavior note with ID: {}", noteId);
        BehaviorNoteResponse response = behaviorNoteService.updateBehaviorNote(noteId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{noteId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    @Operation(summary = "Delete a behavior note")
    public ResponseEntity<Void> deleteBehaviorNote(
            @PathVariable Long animalId,
            @PathVariable Long noteId) {
        log.info("Deleting behavior note with ID: {}", noteId);
        behaviorNoteService.deleteBehaviorNote(noteId);
        return ResponseEntity.noContent().build();
    }
}
