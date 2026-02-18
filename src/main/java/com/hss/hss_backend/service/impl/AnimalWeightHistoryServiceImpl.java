package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.dto.request.AnimalWeightHistoryRequest;
import com.hss.hss_backend.dto.response.AnimalWeightHistoryResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.AnimalWeightHistory;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.AnimalWeightHistoryMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.AnimalWeightHistoryRepository;
import com.hss.hss_backend.service.AnimalWeightHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnimalWeightHistoryServiceImpl implements AnimalWeightHistoryService {

  private final AnimalWeightHistoryRepository weightHistoryRepository;
  private final AnimalRepository animalRepository;
  private final AnimalWeightHistoryMapper mapper;

  @Override
  public AnimalWeightHistoryResponse addWeightRecord(Long animalId, AnimalWeightHistoryRequest request) {
    Animal animal = animalRepository.findById(animalId)
        .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + animalId));

    AnimalWeightHistory history = mapper.toEntity(request);
    history.setAnimal(animal);

    // Update current weight if the new record is recent or simply update it always
    animal.setWeight(request.getWeight());
    animalRepository.save(animal);

    AnimalWeightHistory saved = weightHistoryRepository.save(history);
    return mapper.toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AnimalWeightHistoryResponse> getWeightHistory(Long animalId) {
    if (!animalRepository.existsById(animalId)) {
      throw new ResourceNotFoundException("Animal not found with id: " + animalId);
    }
    return weightHistoryRepository.findByAnimal_AnimalIdOrderByMeasuredAtDesc(animalId)
        .stream()
        .map(mapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteWeightRecord(Long id) {
    if (!weightHistoryRepository.existsById(id)) {
      throw new ResourceNotFoundException("Weight record not found with id: " + id);
    }
    weightHistoryRepository.deleteById(id);
  }

  @Override
  public AnimalWeightHistoryResponse updateWeightRecord(Long id, AnimalWeightHistoryRequest request) {
    AnimalWeightHistory history = weightHistoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Weight record not found with id: " + id));

    mapper.updateEntity(history, request);

    // Should we update the animal's current weight? Only if it's the latest record.
    // For simplicity, we can skip updating the current weight here or check logic.
    // Let's rely on 'add' for current weight updates for now.

    AnimalWeightHistory saved = weightHistoryRepository.save(history);
    return mapper.toResponse(saved);
  }
}
