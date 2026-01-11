package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.OwnerCreateRequest;
import com.hss.hss_backend.dto.request.OwnerUpdateRequest;
import com.hss.hss_backend.dto.response.OwnerResponse;
import com.hss.hss_backend.service.OwnerService;
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
@RequestMapping("/api/owners")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<OwnerResponse> createOwner(@Valid @RequestBody OwnerCreateRequest request) {
        log.info("Creating owner: {} {}", request.getFirstName(), request.getLastName());
        OwnerResponse response = ownerService.createOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<OwnerResponse> getOwnerById(@PathVariable Long id) {
        log.info("Fetching owner with ID: {}", id);
        OwnerResponse response = ownerService.getOwnerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/financial-summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<com.hss.hss_backend.dto.response.OwnerFinancialSummaryResponse> getFinancialSummary(
            @PathVariable Long id) {
        log.info("Fetching financial summary for owner ID: {}", id);
        return ResponseEntity.ok(ownerService.getFinancialSummary(id));
    }

    @GetMapping
    public ResponseEntity<Page<OwnerResponse>> getAllOwners(Pageable pageable) {
        log.info("Fetching all owners with pagination");
        Page<OwnerResponse> response = ownerService.getAllOwners(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<OwnerResponse>> searchOwnersByName(@RequestParam String name) {
        log.info("Searching owners by name: {}", name);
        List<OwnerResponse> response = ownerService.searchOwnersByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/email")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<OwnerResponse>> searchOwnersByEmail(@RequestParam String email) {
        log.info("Searching owners by email: {}", email);
        List<OwnerResponse> response = ownerService.searchOwnersByEmail(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<OwnerResponse> updateOwner(@PathVariable Long id,
            @Valid @RequestBody OwnerUpdateRequest request) {
        log.info("Updating owner with ID: {}", id);
        OwnerResponse response = ownerService.updateOwner(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
        log.info("Deleting owner with ID: {}", id);
        ownerService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }
}
