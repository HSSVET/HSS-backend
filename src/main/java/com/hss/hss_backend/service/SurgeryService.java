package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.SurgeryDto;
import com.hss.hss_backend.dto.SurgeryMedicationDto;

import java.util.List;

public interface SurgeryService {
  SurgeryDto createSurgery(SurgeryDto surgeryDto);

  SurgeryDto getSurgeryById(Long id);

  List<SurgeryDto> getSurgeriesByAnimalId(Long animalId);

  SurgeryDto updateSurgeryStatus(Long id, String status);

  SurgeryDto updateSurgeryDetails(Long id, SurgeryDto surgeryDto);

  SurgeryDto addMedication(Long surgeryId, SurgeryMedicationDto medicationDto);

  void removeMedication(Long surgeryMedId);
}
