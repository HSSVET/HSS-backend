package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.CreateLabTestRequest;
import com.hss.hss_backend.dto.response.LabTestResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.LabResult;
import com.hss.hss_backend.entity.LabTest;
import com.hss.hss_backend.mapper.LabTestMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.LabTestRepository;
import com.hss.hss_backend.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/lab-tests")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabTestController {

  private final LabTestRepository labTestRepository;
  private final AnimalRepository animalRepository;
  private final StorageService storageService;

  @GetMapping
  public ResponseEntity<org.springframework.data.domain.Page<LabTestResponse>> getAllLabTests(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(required = false) LabTest.Status status) {
    org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, limit,
        org.springframework.data.domain.Sort.by("date").descending());

    if (status != null) {
      return ResponseEntity.ok(labTestRepository.findByStatus(status, pageable).map(LabTestMapper::toResponse));
    }

    return ResponseEntity.ok(labTestRepository.findAll(pageable).map(LabTestMapper::toResponse));
  }

  @GetMapping("/stats")
  public ResponseEntity<com.hss.hss_backend.dto.response.LabStatsDto> getStatistics() {
    long total = labTestRepository.count();
    long pending = labTestRepository.countByStatus(LabTest.Status.PENDING);
    long inProgress = labTestRepository.countByStatus(LabTest.Status.IN_PROGRESS);
    long completed = labTestRepository.countByStatus(LabTest.Status.COMPLETED);
    long cancelled = labTestRepository.countByStatus(LabTest.Status.CANCELLED);
    long today = labTestRepository.countByDate(java.time.LocalDate.now());

    return ResponseEntity.ok(com.hss.hss_backend.dto.response.LabStatsDto.builder()
        .total(total)
        .pending(pending)
        .inProgress(inProgress)
        .completed(completed)
        .cancelled(cancelled)
        .today(today)
        .build());
  }

  @GetMapping("/{id}")
  public ResponseEntity<LabTestResponse> getLabTestById(@PathVariable Long id) {
    return labTestRepository.findById(id)
        .map(LabTestMapper::toResponse)
        .map(ResponseEntity::ok)
        .orElse(new ResponseEntity<LabTestResponse>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/pending")
  public ResponseEntity<List<LabTestResponse>> getPendingTests() {
    return ResponseEntity.ok(LabTestMapper.toResponseList(labTestRepository.findByStatus(LabTest.Status.PENDING)));
  }

  @GetMapping("/in-progress")
  public ResponseEntity<List<LabTestResponse>> getInProgressTests() {
    return ResponseEntity.ok(LabTestMapper.toResponseList(labTestRepository.findByStatus(LabTest.Status.IN_PROGRESS)));
  }

  @GetMapping("/completed")
  public ResponseEntity<List<LabTestResponse>> getCompletedTests() {
    return ResponseEntity.ok(LabTestMapper.toResponseList(labTestRepository.findByStatus(LabTest.Status.COMPLETED)));
  }

  @GetMapping("/animal/{animalId}")
  public ResponseEntity<List<LabTestResponse>> getTestsByAnimal(@PathVariable Long animalId) {
    return ResponseEntity.ok(LabTestMapper.toResponseList(labTestRepository.findByAnimalAnimalId(animalId)));
  }

  @GetMapping("/search")
  public ResponseEntity<List<LabTestResponse>> searchByTestName(@RequestParam String testName) {
    return ResponseEntity
        .ok(LabTestMapper.toResponseList(labTestRepository.findByTestNameContainingIgnoreCase(testName)));
  }

  @PostMapping
  public ResponseEntity<LabTestResponse> createLabTest(@RequestBody CreateLabTestRequest request) {
    return animalRepository.findById(request.getAnimalId()).map(animal -> {
      LabTest labTest = new LabTest();
      labTest.setAnimal(animal);
      labTest.setTestName(request.getTestName() != null ? request.getTestName() : "Genel Test");
      labTest.setStatus(LabTest.Status.PENDING);
      // Defaulting date to now as string or proper date type depending on entity
      // Entity usually expects String or LocalDate. Let's assume String for date
      // field based on previous view or LocalDate.
      // LabTestMapper uses .getDate() so I should check entity.
      // Assuming string for now based on simplicity, or LocalDateTime if Entity calls
      // it so.
      // Actually I'll set it to now formatted string if it's string.
      labTest.setDate(java.time.LocalDate.now());

      labTest.setCreatedAt(LocalDateTime.now());
      labTest.setUpdatedAt(LocalDateTime.now());

      LabTest savedTest = labTestRepository.save(labTest);
      return ResponseEntity.ok(LabTestMapper.toResponse(savedTest));
    }).orElse(new ResponseEntity<LabTestResponse>(HttpStatus.BAD_REQUEST));
  }

  @PostMapping("/{id}/results")
  @Transactional
  public ResponseEntity<?> uploadResult(
      @PathVariable Long id,
      @RequestParam("file") MultipartFile file,
      @RequestParam(required = false) String result,
      @RequestParam(required = false) String value,
      @RequestParam(required = false) String unit,
      @RequestParam(required = false) String normalRange,
      @RequestParam(required = false) String interpretation) {

    return labTestRepository.findById(id).map(test -> {
      try {
        Long clinicId = test.getAnimal().getClinic().getClinicId();
        String fileUrl = storageService.uploadFile(file, "clinics/" + clinicId + "/lab-results/" + id);

        LabResult labResult = LabResult.builder()
            .labTest(test)
            .result(result != null ? result : "Dosya yüklendi")
            .value(value)
            .unit(unit)
            .normalRange(normalRange)
            .interpretation(interpretation)
            .fileUrl(fileUrl)
            .build();

        if (test.getLabResults() == null) {
          test.setLabResults(new java.util.ArrayList<>());
        }
        test.getLabResults().add(labResult);

        // Update test status to COMPLETED if not already
        test.setStatus(LabTest.Status.COMPLETED);

        LabTest savedTest = labTestRepository.save(test);
        return ResponseEntity.ok(LabTestMapper.toResponse(savedTest));
      } catch (Exception e) {
        e.printStackTrace(); // Log full stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
      }
    }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
