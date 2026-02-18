package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.RadiologicalImagingCreateRequest;
import com.hss.hss_backend.dto.response.RadiologicalImagingResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RadiologicalImagingService {
    
    RadiologicalImagingResponse createRadiologicalImaging(RadiologicalImagingCreateRequest request, MultipartFile imageFile);
    
    RadiologicalImagingResponse getRadiologicalImagingById(Long imageId);
    
    List<RadiologicalImagingResponse> getRadiologicalImagingsByAnimalId(Long animalId);
    
    void deleteRadiologicalImaging(Long imageId);
    
    long countByAnimalId(Long animalId);
}
