package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.ClinicalExaminationCreateRequest;
import com.hss.hss_backend.dto.response.ClinicalExaminationResponse;

import java.util.List;

public interface ClinicalExaminationService {
    
    ClinicalExaminationResponse createClinicalExamination(ClinicalExaminationCreateRequest request);
    
    ClinicalExaminationResponse getClinicalExaminationById(Long examinationId);
    
    List<ClinicalExaminationResponse> getClinicalExaminationsByAnimalId(Long animalId);
    
    void deleteClinicalExamination(Long examinationId);
    
    long countByAnimalId(Long animalId);
}
