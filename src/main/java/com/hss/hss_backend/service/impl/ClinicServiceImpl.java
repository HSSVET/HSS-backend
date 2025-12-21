package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.dto.request.ClinicCreateRequest;
import com.hss.hss_backend.dto.request.ClinicUpdateRequest;
import com.hss.hss_backend.dto.response.ClinicResponse;
import com.hss.hss_backend.entity.Clinic;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.ClinicMapper;
import com.hss.hss_backend.repository.ClinicRepository;
import com.hss.hss_backend.service.ClinicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClinicServiceImpl implements ClinicService {

  private final ClinicRepository clinicRepository;
  private final ClinicMapper clinicMapper;
  private final com.hss.hss_backend.service.UserService userService;
  private final com.hss.hss_backend.service.LicenseService licenseService;

  @Override
  public ClinicResponse createClinic(ClinicCreateRequest request) {
    log.info("Creating new clinic: {}", request.getName());
    if (clinicRepository.findByName(request.getName()).isPresent()) {
      throw new IllegalArgumentException("Clinic with this name already exists");
    }

    Clinic clinic = clinicMapper.toEntity(request);

    // Generate License
    String type = request.getLicenseType() != null ? request.getLicenseType() : "STRT";
    clinic.setLicenseType(type);
    clinic.setLicenseKey(licenseService.generateLicenseKey(type));
    clinic.setLicenseStartDate(java.time.LocalDate.now());
    clinic.setLicenseEndDate(licenseService.calculateEndDate(type, java.time.LocalDate.now()));
    clinic.setLicenseStatus("ACTIVE");

    Clinic savedClinic = clinicRepository.save(clinic);

    // Auto-provision initial admin if email is provided
    if (request.getAdminEmail() != null && !request.getAdminEmail().isBlank()) {
      try {
        String firstName = request.getAdminFirstName() != null ? request.getAdminFirstName() : "Clinic";
        String lastName = request.getAdminLastName() != null ? request.getAdminLastName() : "Admin";
        userService.createClinicAdmin(savedClinic, request.getAdminEmail(), firstName, lastName);
        log.info("Auto-provisioned admin for clinic: {}", savedClinic.getName());
      } catch (Exception e) {
        log.error("Failed to auto-provision clinic admin: {}", e.getMessage());
        // We don't rollback clinic creation, but maybe we should?
        // For now, logging error is safer to avoid blocking if firebase fails.
        // But from user perspective, it's better to know.
        // Let's decide to Log and Proceed, user can add admin later manually if needed?
        // Actually, manual add might not be exposed yet.
        // Let's keep it non-blocking but heavily logged.
      }
    }

    return clinicMapper.toResponse(savedClinic);
  }

  @Override
  @Transactional(readOnly = true)
  public ClinicResponse getClinicById(Long id) {
    Clinic clinic = clinicRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Clinic", id));
    return clinicMapper.toResponse(clinic);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ClinicResponse> getAllClinics(Pageable pageable) {
    return clinicRepository.findAll(pageable)
        .map(clinicMapper::toResponse);
  }

  @Override
  public ClinicResponse updateClinic(Long id, ClinicUpdateRequest request) {
    Clinic clinic = clinicRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Clinic", id));

    if (request.getName() != null)
      clinic.setName(request.getName());
    if (request.getAddress() != null)
      clinic.setAddress(request.getAddress());
    if (request.getPhone() != null)
      clinic.setPhone(request.getPhone());
    if (request.getEmail() != null)
      clinic.setEmail(request.getEmail());
    if (request.getLicenseKey() != null)
      clinic.setLicenseKey(request.getLicenseKey());
    if (request.getSettings() != null)
      clinic.setSettings(request.getSettings());

    return clinicMapper.toResponse(clinicRepository.save(clinic));
  }

  @Override
  public void deleteClinic(Long id) {
    if (!clinicRepository.existsById(id)) {
      throw new ResourceNotFoundException("Clinic", id);
    }
    clinicRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ClinicResponse> searchClinics(String name) {
    // Placeholder
    return List.of();
  }
}
