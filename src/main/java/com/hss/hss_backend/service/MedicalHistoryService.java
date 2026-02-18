package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.response.MedicalHistoryResponse;

import java.util.List;

public interface MedicalHistoryService {
    
    MedicalHistoryResponse getMedicalHistoryById(Long historyId);
    
    List<MedicalHistoryResponse> getMedicalHistoriesByAnimalId(Long animalId);
    
    long countByAnimalId(Long animalId);
}
