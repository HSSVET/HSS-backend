package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.ClinicCreateRequest;
import com.hss.hss_backend.dto.request.ClinicUpdateRequest;
import com.hss.hss_backend.dto.response.ClinicResponse;
import com.hss.hss_backend.service.ClinicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {

  private final ClinicService clinicService;

  @PostMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<ClinicResponse> createClinic(@Valid @RequestBody ClinicCreateRequest request) {
    return new ResponseEntity<>(clinicService.createClinic(request), HttpStatus.CREATED);
  }

  @GetMapping
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<Page<ClinicResponse>> getAllClinics(Pageable pageable) {
    return ResponseEntity.ok(clinicService.getAllClinics(pageable));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('ADMIN') and @securityService.isUserInClinic(#id))")
  // Need a way to check if user belongs to clinic. For now, restrict to
  // SUPER_ADMIN to be safe.
  // Or we can rely on isolation logic if we use it, but "getAllClinics" is
  // definitely Super Admin.
  public ResponseEntity<ClinicResponse> getClinicById(@PathVariable Long id) {
    return ResponseEntity.ok(clinicService.getClinicById(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<ClinicResponse> updateClinic(@PathVariable Long id,
      @Valid @RequestBody ClinicUpdateRequest request) {
    return ResponseEntity.ok(clinicService.updateClinic(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<Void> deleteClinic(@PathVariable Long id) {
    clinicService.deleteClinic(id);
    return ResponseEntity.noContent().build();
  }
}
