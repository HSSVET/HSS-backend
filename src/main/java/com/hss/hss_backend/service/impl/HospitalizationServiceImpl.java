package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.dto.HospitalizationDto;
import com.hss.hss_backend.dto.HospitalizationLogDto;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Hospitalization;
import com.hss.hss_backend.entity.HospitalizationLog;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.HospitalizationMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.HospitalizationRepository;
import com.hss.hss_backend.service.HospitalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HospitalizationServiceImpl implements HospitalizationService {

  private final HospitalizationRepository hospitalizationRepository;
  private final AnimalRepository animalRepository;
  private final HospitalizationMapper hospitalizationMapper;

  @Override
  public HospitalizationDto admitPatient(HospitalizationDto dto) {
    Animal animal = animalRepository.findById(dto.getAnimalId())
        .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + dto.getAnimalId()));

    // Check if already active hospitalization?
    // Optional validation logic here

    Hospitalization hospitalization = hospitalizationMapper.toEntity(dto);
    hospitalization.setAnimal(animal);
    hospitalization.setAdmissionDate(LocalDateTime.now());
    hospitalization.setStatus("ACTIVE");

    return hospitalizationMapper.toDto(hospitalizationRepository.save(hospitalization));
  }

  @Override
  public HospitalizationDto dischargePatient(Long id) {
    Hospitalization hospitalization = hospitalizationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hospitalization not found with id: " + id));

    hospitalization.setDischargeDate(LocalDateTime.now());
    hospitalization.setStatus("DISCHARGED");

    return hospitalizationMapper.toDto(hospitalizationRepository.save(hospitalization));
  }

  @Override
  @Transactional(readOnly = true)
  public HospitalizationDto getHospitalizationById(Long id) {
    Hospitalization hospitalization = hospitalizationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hospitalization not found with id: " + id));
    return hospitalizationMapper.toDto(hospitalization);
  }

  @Override
  @Transactional(readOnly = true)
  public List<HospitalizationDto> getHospitalizationsByAnimalId(Long animalId) {
    return hospitalizationMapper.toDtoList(hospitalizationRepository.findByAnimal_AnimalId(animalId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<HospitalizationDto> getActiveHospitalizations() {
    return hospitalizationMapper.toDtoList(hospitalizationRepository.findByStatus("ACTIVE"));
  }

  @Override
  public HospitalizationLogDto addLog(Long hospitalizationId, HospitalizationLogDto logDto) {
    Hospitalization hospitalization = hospitalizationRepository.findById(hospitalizationId)
        .orElseThrow(() -> new ResourceNotFoundException("Hospitalization not found with id: " + hospitalizationId));

    HospitalizationLog log = hospitalizationMapper.toLogEntity(logDto);
    log.setHospitalization(hospitalization);
    if (log.getLogDate() == null) {
      log.setLogDate(LocalDateTime.now());
    }

    hospitalization.getLogs().add(log);
    hospitalizationRepository.save(hospitalization); // cascades to log

    return hospitalizationMapper.toLogDto(log);
  }
}
