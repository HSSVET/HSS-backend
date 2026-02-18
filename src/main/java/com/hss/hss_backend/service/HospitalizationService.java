package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.HospitalizationDto;
import com.hss.hss_backend.dto.HospitalizationLogDto;

import java.util.List;

public interface HospitalizationService {
  HospitalizationDto admitPatient(HospitalizationDto hospitalizationDto);

  HospitalizationDto dischargePatient(Long id);

  HospitalizationDto getHospitalizationById(Long id);

  List<HospitalizationDto> getHospitalizationsByAnimalId(Long animalId);

  List<HospitalizationDto> getActiveHospitalizations();

  HospitalizationLogDto addLog(Long hospitalizationId, HospitalizationLogDto logDto);
}
