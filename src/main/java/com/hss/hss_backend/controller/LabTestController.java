package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.LabTestDTO;
import com.hss.hss_backend.entity.LabTest;
import com.hss.hss_backend.repository.LabTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lab-tests")
@RequiredArgsConstructor
public class LabTestController {

  private final LabTestRepository labTestRepository;

  private LabTestDTO mapToDTO(LabTest test) {
    String animalName = "";
    String animalSpecies = "";
    Long animalId = null;

    if (test.getAnimal() != null) {
      animalId = test.getAnimal().getAnimalId();
      try {
        animalName = test.getAnimal().getName();
        if (test.getAnimal().getSpecies() != null) {
          animalSpecies = test.getAnimal().getSpecies().getName();
        }
      } catch (Exception e) {
        animalName = "Unknown";
      }
    }

    return LabTestDTO.builder()
        .testId(test.getTestId())
        .animalId(animalId)
        .animalName(animalName)
        .animalSpecies(animalSpecies)
        .testName(test.getTestName())
        .date(test.getDate())
        .status(test.getStatus())
        .build();
  }

  @GetMapping
  public ResponseEntity<List<LabTestDTO>> getAllLabTests() {
    return ResponseEntity.ok(labTestRepository.findAll().stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<LabTestDTO> getLabTestById(@PathVariable Long id) {
    return labTestRepository.findById(id)
        .map(this::mapToDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/pending")
  public ResponseEntity<List<LabTestDTO>> getPendingTests() {
    return ResponseEntity.ok(labTestRepository.findByStatus(LabTest.Status.PENDING).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/in-progress")
  public ResponseEntity<List<LabTestDTO>> getInProgressTests() {
    return ResponseEntity.ok(labTestRepository.findByStatus(LabTest.Status.IN_PROGRESS).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/completed")
  public ResponseEntity<List<LabTestDTO>> getCompletedTests() {
    return ResponseEntity.ok(labTestRepository.findByStatus(LabTest.Status.COMPLETED).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/animal/{animalId}")
  public ResponseEntity<List<LabTestDTO>> getTestsByAnimal(@PathVariable Long animalId) {
    return ResponseEntity.ok(labTestRepository.findByAnimalAnimalId(animalId).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/search")
  public ResponseEntity<List<LabTestDTO>> searchByTestName(@RequestParam String testName) {
    return ResponseEntity.ok(labTestRepository.findByTestNameContainingIgnoreCase(testName).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }
}
