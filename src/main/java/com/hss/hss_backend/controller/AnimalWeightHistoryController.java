package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.request.AnimalWeightHistoryRequest;
import com.hss.hss_backend.dto.response.AnimalWeightHistoryResponse;
import com.hss.hss_backend.service.AnimalWeightHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalWeightHistoryController {

  private final AnimalWeightHistoryService weightHistoryService;

  @PostMapping("/{animalId}/weight-history")
  public ResponseEntity<AnimalWeightHistoryResponse> addWeightRecord(
      @PathVariable Long animalId,
      @Valid @RequestBody AnimalWeightHistoryRequest request) {
    return new ResponseEntity<>(weightHistoryService.addWeightRecord(animalId, request), HttpStatus.CREATED);
  }

  @GetMapping("/{animalId}/weight-history")
  public ResponseEntity<List<AnimalWeightHistoryResponse>> getWeightHistory(
      @PathVariable Long animalId) {
    return ResponseEntity.ok(weightHistoryService.getWeightHistory(animalId));
  }

  @PutMapping("/weight-history/{id}")
  public ResponseEntity<AnimalWeightHistoryResponse> updateWeightRecord(
      @PathVariable Long id,
      @Valid @RequestBody AnimalWeightHistoryRequest request) {
    return ResponseEntity.ok(weightHistoryService.updateWeightRecord(id, request));
  }

  @DeleteMapping("/weight-history/{id}")
  public ResponseEntity<Void> deleteWeightRecord(@PathVariable Long id) {
    weightHistoryService.deleteWeightRecord(id);
    return ResponseEntity.noContent().build();
  }
}
