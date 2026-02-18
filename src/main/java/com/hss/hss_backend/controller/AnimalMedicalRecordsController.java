package com.hss.hss_backend.controller;

import com.hss.hss_backend.entity.*;
import com.hss.hss_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/animals/{animalId}/medical")
@RequiredArgsConstructor
@Slf4j
public class AnimalMedicalRecordsController {

  private final MedicalHistoryRepository medicalHistoryRepository;
  private final ClinicalExaminationRepository clinicalExaminationRepository;
  private final PrescriptionRepository prescriptionRepository;
  private final RadiologicalImagingRepository radiologicalImagingRepository;
  private final PathologyFindingRepository pathologyFindingRepository;

  // ============================================================
  // MEDICAL HISTORY (Hastalık Geçmişi)
  // ============================================================
  @GetMapping("/history")
  public ResponseEntity<List<Map<String, Object>>> getMedicalHistory(@PathVariable Long animalId) {
    log.info("Getting medical history for animal: {}", animalId);
    List<MedicalHistory> histories = medicalHistoryRepository.findByAnimalAnimalId(animalId);

    List<Map<String, Object>> result = histories.stream()
        .map(h -> Map.<String, Object>of(
            "id", h.getHistoryId(),
            "diagnosis", h.getDiagnosis() != null ? h.getDiagnosis() : "",
            "date", h.getDate() != null ? h.getDate().toString() : "",
            "treatment", h.getTreatment() != null ? h.getTreatment() : ""))
        .collect(Collectors.toList());

    return ResponseEntity.ok(result);
  }

  // ============================================================
  // CLINICAL EXAMINATIONS (Klinik Muayene)
  // ============================================================
  @GetMapping("/examinations")
  public ResponseEntity<List<Map<String, Object>>> getClinicalExaminations(@PathVariable Long animalId) {
    log.info("Getting clinical examinations for animal: {}", animalId);
    List<ClinicalExamination> examinations = clinicalExaminationRepository.findByAnimalAnimalId(animalId);

    List<Map<String, Object>> result = examinations.stream()
        .map(e -> Map.<String, Object>of(
            "id", e.getExaminationId(),
            "date", e.getDate() != null ? e.getDate().toString() : "",
            "findings", e.getFindings() != null ? e.getFindings() : "",
            "veterinarianName", e.getVeterinarianName() != null ? e.getVeterinarianName() : ""))
        .collect(Collectors.toList());

    return ResponseEntity.ok(result);
  }

  // ============================================================
  // PRESCRIPTIONS (Reçeteler)
  // ============================================================
  @GetMapping("/prescriptions")
  public ResponseEntity<List<Map<String, Object>>> getPrescriptions(@PathVariable Long animalId) {
    log.info("Getting prescriptions for animal: {}", animalId);
    List<Prescription> prescriptions = prescriptionRepository.findByAnimalAnimalId(animalId);

    List<Map<String, Object>> result = prescriptions.stream()
        .map(p -> Map.<String, Object>of(
            "id", p.getPrescriptionId(),
            "date", p.getDate() != null ? p.getDate().toString() : "",
            "medicines", p.getMedicines() != null ? p.getMedicines() : "",
            "dosage", p.getDosage() != null ? p.getDosage() : "",
            "instructions", p.getInstructions() != null ? p.getInstructions() : "",
            "durationDays", p.getDurationDays() != null ? p.getDurationDays() : 0,
            "status", p.getStatus() != null ? p.getStatus().name() : "ACTIVE"))
        .collect(Collectors.toList());

    return ResponseEntity.ok(result);
  }

  // ============================================================
  // RADIOLOGICAL IMAGING (Radyoloji Görüntüleme)
  // ============================================================
  @GetMapping("/radiology")
  public ResponseEntity<List<Map<String, Object>>> getRadiologicalImaging(@PathVariable Long animalId) {
    log.info("Getting radiological imaging for animal: {}", animalId);
    List<RadiologicalImaging> imagings = radiologicalImagingRepository.findByAnimalAnimalId(animalId);

    List<Map<String, Object>> result = imagings.stream()
        .map(r -> Map.<String, Object>of(
            "id", r.getImageId(),
            "date", r.getDate() != null ? r.getDate().toString() : "",
            "type", r.getType() != null ? r.getType() : "",
            "imageUrl", r.getImageUrl() != null ? r.getImageUrl() : "",
            "comment", r.getComment() != null ? r.getComment() : ""))
        .collect(Collectors.toList());

    return ResponseEntity.ok(result);
  }

  // ============================================================
  // PATHOLOGY FINDINGS (Patoloji Bulguları)
  // ============================================================
  @GetMapping("/pathology")
  public ResponseEntity<List<Map<String, Object>>> getPathologyFindings(@PathVariable Long animalId) {
    log.info("Getting pathology findings for animal: {}", animalId);
    List<PathologyFinding> findings = pathologyFindingRepository.findByAnimalAnimalId(animalId);

    List<Map<String, Object>> result = findings.stream()
        .map(f -> Map.<String, Object>of(
            "id", f.getPathologyId(),
            "date", f.getDate() != null ? f.getDate().toString() : "",
            "report", f.getReport() != null ? f.getReport() : "",
            "pathologistName", f.getPathologistName() != null ? f.getPathologistName() : "",
            "findingsSummary", f.getFindingsSummary() != null ? f.getFindingsSummary() : "",
            "recommendations", f.getRecommendations() != null ? f.getRecommendations() : ""))
        .collect(Collectors.toList());

    return ResponseEntity.ok(result);
  }
}
