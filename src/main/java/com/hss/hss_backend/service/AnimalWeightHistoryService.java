package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.AnimalWeightHistoryRequest;
import com.hss.hss_backend.dto.response.AnimalWeightHistoryResponse;

import java.util.List;

public interface AnimalWeightHistoryService {
  AnimalWeightHistoryResponse addWeightRecord(Long animalId, AnimalWeightHistoryRequest request);

  List<AnimalWeightHistoryResponse> getWeightHistory(Long animalId);

  void deleteWeightRecord(Long id);

  AnimalWeightHistoryResponse updateWeightRecord(Long id, AnimalWeightHistoryRequest request);
}
