package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.TreatmentCreateRequest;
import com.hss.hss_backend.dto.request.TreatmentUpdateRequest;
import com.hss.hss_backend.dto.response.TreatmentResponse;

import java.util.List;

public interface TreatmentService {
    
    TreatmentResponse createTreatment(TreatmentCreateRequest request);
    
    TreatmentResponse getTreatmentById(Long treatmentId);
    
    List<TreatmentResponse> getTreatmentsByAnimalId(Long animalId);
    
    TreatmentResponse updateTreatment(Long treatmentId, TreatmentUpdateRequest request);
    
    void deleteTreatment(Long treatmentId);
    
    long countByAnimalId(Long animalId);
}
