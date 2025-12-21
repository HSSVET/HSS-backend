package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.ClinicCreateRequest;
import com.hss.hss_backend.dto.request.ClinicUpdateRequest;
import com.hss.hss_backend.dto.response.ClinicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClinicService {

  ClinicResponse createClinic(ClinicCreateRequest request);

  ClinicResponse getClinicById(Long id);

  Page<ClinicResponse> getAllClinics(Pageable pageable);

  ClinicResponse updateClinic(Long id, ClinicUpdateRequest request);

  void deleteClinic(Long id);

  List<ClinicResponse> searchClinics(String name);
}
