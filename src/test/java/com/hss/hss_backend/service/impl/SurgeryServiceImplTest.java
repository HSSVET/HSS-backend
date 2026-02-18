package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.dto.SurgeryDto;
import com.hss.hss_backend.dto.SurgeryMedicationDto;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Surgery;
import com.hss.hss_backend.entity.SurgeryMedication;
import com.hss.hss_backend.mapper.SurgeryMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.SurgeryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurgeryServiceImplTest {

  @Mock
  private SurgeryRepository surgeryRepository;
  @Mock
  private AnimalRepository animalRepository;
  @Mock
  private SurgeryMapper surgeryMapper;

  @InjectMocks
  private SurgeryServiceImpl surgeryService;

  @Test
  void createSurgery_ShouldReturnSavedSurgery() {
    // Arrange
    SurgeryDto inputDto = SurgeryDto.builder().animalId(1L).build();
    Animal animal = new Animal();
    animal.setAnimalId(1L);
    Surgery surgery = new Surgery();
    surgery.setAnimal(animal);
    Surgery savedSurgery = new Surgery();
    savedSurgery.setSurgeryId(10L);
    SurgeryDto expectedDto = SurgeryDto.builder().surgeryId(10L).status("PLANNED").build();

    when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
    when(surgeryMapper.toEntity(inputDto)).thenReturn(surgery);
    when(surgeryRepository.save(any(Surgery.class))).thenReturn(savedSurgery);
    when(surgeryMapper.toDto(savedSurgery)).thenReturn(expectedDto);

    // Act
    SurgeryDto result = surgeryService.createSurgery(inputDto);

    // Assert
    assertNotNull(result);
    assertEquals(10L, result.getSurgeryId());
    assertEquals("PLANNED", result.getStatus());
    verify(surgeryRepository).save(any(Surgery.class));
  }

  @Test
  void updateSurgeryStatus_ShouldUpdateStatus() {
    // Arrange
    Long surgeryId = 1L;
    Surgery surgery = new Surgery();
    surgery.setSurgeryId(surgeryId);
    surgery.setStatus("PLANNED");

    Surgery updatedSurgery = new Surgery();
    updatedSurgery.setSurgeryId(surgeryId);
    updatedSurgery.setStatus("COMPLETED");

    SurgeryDto expectedDto = SurgeryDto.builder().surgeryId(surgeryId).status("COMPLETED").build();

    when(surgeryRepository.findById(surgeryId)).thenReturn(Optional.of(surgery));
    when(surgeryRepository.save(any(Surgery.class))).thenReturn(updatedSurgery);
    when(surgeryMapper.toDto(updatedSurgery)).thenReturn(expectedDto);

    // Act
    SurgeryDto result = surgeryService.updateSurgeryStatus(surgeryId, "COMPLETED");

    // Assert
    assertEquals("COMPLETED", result.getStatus());
  }

  @Test
  void addMedication_ShouldAddMedicationToSurgery() {
    // Arrange
    Long surgeryId = 1L;
    SurgeryMedicationDto medDto = SurgeryMedicationDto.builder().quantity(2).build();
    Surgery surgery = new Surgery();
    surgery.setMedications(new ArrayList<>());

    SurgeryMedication medEntity = new SurgeryMedication();

    Surgery savedSurgery = new Surgery(); // In reality would have the med
    SurgeryDto expectedDto = SurgeryDto.builder().surgeryId(surgeryId).build();

    when(surgeryRepository.findById(surgeryId)).thenReturn(Optional.of(surgery));
    when(surgeryMapper.toMedicationEntity(medDto)).thenReturn(medEntity);
    when(surgeryRepository.save(surgery)).thenReturn(savedSurgery);
    when(surgeryMapper.toDto(savedSurgery)).thenReturn(expectedDto);

    // Act
    SurgeryDto result = surgeryService.addMedication(surgeryId, medDto);

    // Assert
    assertNotNull(result);
    verify(surgeryRepository).save(surgery);
  }
}
