package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.dto.SurgeryDto;
import com.hss.hss_backend.dto.SurgeryMedicationDto;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Surgery;
import com.hss.hss_backend.entity.SurgeryMedication;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.SurgeryMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.SurgeryRepository;
import com.hss.hss_backend.service.SurgeryService;
// import com.hss.hss_backend.service.InventoryService; // Likely needed later for stock integration
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SurgeryServiceImpl implements SurgeryService {

  private final SurgeryRepository surgeryRepository;
  private final AnimalRepository animalRepository;
  private final SurgeryMapper surgeryMapper;
  private final com.hss.hss_backend.service.StockProductService stockProductService;

  @Override
  public SurgeryDto createSurgery(SurgeryDto surgeryDto) {
    Animal animal = animalRepository.findById(surgeryDto.getAnimalId())
        .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + surgeryDto.getAnimalId()));

    Surgery surgery = surgeryMapper.toEntity(surgeryDto);
    surgery.setAnimal(animal);
    surgery.setStatus("PLANNED");

    Surgery savedSurgery = surgeryRepository.save(surgery);
    return surgeryMapper.toDto(savedSurgery);
  }

  @Override
  public SurgeryDto getSurgeryById(Long id) {
    Surgery surgery = surgeryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + id));
    return surgeryMapper.toDto(surgery);
  }

  @Override
  public List<SurgeryDto> getSurgeriesByAnimalId(Long animalId) {
    List<Surgery> surgeries = surgeryRepository.findByAnimal_AnimalId(animalId);
    return surgeries.stream().map(surgeryMapper::toDto).toList();
  }

  @Override
  public SurgeryDto updateSurgeryStatus(Long id, String status) {
    Surgery surgery = surgeryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + id));

    surgery.setStatus(status);
    Surgery updatedSurgery = surgeryRepository.save(surgery);
    return surgeryMapper.toDto(updatedSurgery);
  }

  @Override
  public SurgeryDto updateSurgeryDetails(Long id, SurgeryDto surgeryDto) {
    Surgery surgery = surgeryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + id));

    if (surgeryDto.getDate() != null)
      surgery.setDate(surgeryDto.getDate());
    if (surgeryDto.getNotes() != null)
      surgery.setNotes(surgeryDto.getNotes());
    if (surgeryDto.getPreOpInstructions() != null)
      surgery.setPreOpInstructions(surgeryDto.getPreOpInstructions());
    if (surgeryDto.getPostOpInstructions() != null)
      surgery.setPostOpInstructions(surgeryDto.getPostOpInstructions());
    if (surgeryDto.getAnesthesiaProtocol() != null)
      surgery.setAnesthesiaProtocol(surgeryDto.getAnesthesiaProtocol());
    if (surgeryDto.getAnesthesiaConsent() != null)
      surgery.setAnesthesiaConsent(surgeryDto.getAnesthesiaConsent());
    if (surgeryDto.getVeterinarianId() != null)
      surgery.setVeterinarianId(surgeryDto.getVeterinarianId());
    if (surgeryDto.getStatus() != null)
      surgery.setStatus(surgeryDto.getStatus());

    Surgery updatedSurgery = surgeryRepository.save(surgery);
    return surgeryMapper.toDto(updatedSurgery);
  }

  @Override
  public SurgeryDto addMedication(Long surgeryId, SurgeryMedicationDto medicationDto) {
    Surgery surgery = surgeryRepository.findById(surgeryId)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + surgeryId));

    SurgeryMedication med = surgeryMapper.toMedicationEntity(medicationDto);
    med.setSurgery(surgery);

    surgery.getMedications().add(med);

    // Deduct stock if stockProductId is provided (assuming DTO has it or using
    // medicineId as fallback)
    Long stockId = medicationDto.getMedicineId(); // Using medicineId as stockId for simplicity if mapped
    if (stockId != null) {
      stockProductService.deductStock(stockId,
          medicationDto.getQuantity() != null ? medicationDto.getQuantity().intValue() : 1,
          "Surgery Medication: SURGERY for Animal: " + surgery.getAnimal().getName(),
          "SURGERY",
          surgery.getSurgeryId());
    }

    return surgeryMapper.toDto(surgeryRepository.save(surgery));
  }

  @Override
  public void removeMedication(Long surgeryMedId) {
    // Logic to remove medication and potentially restore stock
    // Implementation requires Repository for SurgeryMedication or cascade removal
    // via Surgery parent
  }
}
