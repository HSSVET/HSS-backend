package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.dto.HospitalizationDto;
import com.hss.hss_backend.dto.HospitalizationLogDto;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Hospitalization;
import com.hss.hss_backend.entity.HospitalizationLog;
import com.hss.hss_backend.mapper.HospitalizationMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.HospitalizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalizationServiceImplTest {

  @Mock
  private HospitalizationRepository hospitalizationRepository;
  @Mock
  private AnimalRepository animalRepository;
  @Mock
  private HospitalizationMapper hospitalizationMapper;

  @InjectMocks
  private HospitalizationServiceImpl hospitalizationService;

  @Test
  void admitPatient_ShouldCreateActiveHospitalization() {
    HospitalizationDto dto = HospitalizationDto.builder().animalId(1L).build();
    Animal animal = new Animal();
    Hospitalization hospitalization = new Hospitalization();
    Hospitalization saved = new Hospitalization();
    saved.setStatus("ACTIVE");
    HospitalizationDto expected = HospitalizationDto.builder().status("ACTIVE").build();

    when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
    when(hospitalizationMapper.toEntity(dto)).thenReturn(hospitalization);
    when(hospitalizationRepository.save(any(Hospitalization.class))).thenReturn(saved);
    when(hospitalizationMapper.toDto(saved)).thenReturn(expected);

    HospitalizationDto result = hospitalizationService.admitPatient(dto);

    assertEquals("ACTIVE", result.getStatus());
    verify(hospitalizationRepository).save(any(Hospitalization.class));
  }

  @Test
  void dischargePatient_ShouldUpdateStatusAndDate() {
    Long id = 1L;
    Hospitalization hospitalization = new Hospitalization();
    Hospitalization saved = new Hospitalization();
    saved.setStatus("DISCHARGED");
    saved.setDischargeDate(LocalDateTime.now());
    HospitalizationDto expected = HospitalizationDto.builder().status("DISCHARGED").build();

    when(hospitalizationRepository.findById(id)).thenReturn(Optional.of(hospitalization));
    when(hospitalizationRepository.save(any(Hospitalization.class))).thenReturn(saved);
    when(hospitalizationMapper.toDto(saved)).thenReturn(expected);

    HospitalizationDto result = hospitalizationService.dischargePatient(id);

    assertEquals("DISCHARGED", result.getStatus());
  }

  @Test
  void addLog_ShouldAddLogToHospitalization() {
    Long id = 1L;
    HospitalizationLogDto logDto = HospitalizationLogDto.builder().notes("Test log").build();
    Hospitalization hospitalization = new Hospitalization();
    hospitalization.setLogs(new ArrayList<>());

    HospitalizationLog log = new HospitalizationLog();
    HospitalizationLogDto expectedLogDto = HospitalizationLogDto.builder().notes("Test log").build();

    when(hospitalizationRepository.findById(id)).thenReturn(Optional.of(hospitalization));
    when(hospitalizationMapper.toLogEntity(logDto)).thenReturn(log);
    when(hospitalizationMapper.toLogDto(log)).thenReturn(expectedLogDto);

    HospitalizationLogDto result = hospitalizationService.addLog(id, logDto);

    assertEquals("Test log", result.getNotes());
    verify(hospitalizationRepository).save(hospitalization);
  }
}
