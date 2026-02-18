package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.VaccineCreateRequest;
import com.hss.hss_backend.dto.request.VaccineUpdateRequest;
import com.hss.hss_backend.dto.response.VaccineResponse;
import com.hss.hss_backend.entity.Vaccine;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.VaccineMapper;
import com.hss.hss_backend.repository.VaccineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VaccineService {
    
    private final VaccineRepository vaccineRepository;
    private final VaccineMapper vaccineMapper;
    
    @Transactional
    public VaccineResponse createVaccine(VaccineCreateRequest request) {
        log.info("Creating vaccine: {}", request.getVaccineName());
        Vaccine vaccine = vaccineMapper.toEntity(request);
        Vaccine savedVaccine = vaccineRepository.save(vaccine);
        return vaccineMapper.toResponse(savedVaccine);
    }
    
    public VaccineResponse getVaccineById(Long id) {
        log.info("Fetching vaccine with ID: {}", id);
        Vaccine vaccine = vaccineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vaccine", "id", id));
        return vaccineMapper.toResponse(vaccine);
    }
    
    public List<VaccineResponse> getAllVaccines() {
        log.info("Fetching all vaccines");
        return vaccineRepository.findAll().stream()
            .map(vaccineMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    public Page<VaccineResponse> getAllVaccinesPaged(Pageable pageable) {
        log.info("Fetching all vaccines with pagination");
        return vaccineRepository.findAll(pageable)
            .map(vaccineMapper::toResponse);
    }
    
    public List<VaccineResponse> searchVaccinesByName(String name) {
        log.info("Searching vaccines by name: {}", name);
        return vaccineRepository.findByVaccineNameContainingIgnoreCase(name).stream()
            .map(vaccineMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public VaccineResponse updateVaccine(Long id, VaccineUpdateRequest request) {
        log.info("Updating vaccine with ID: {}", id);
        Vaccine vaccine = vaccineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vaccine", "id", id));
        
        vaccineMapper.updateEntityFromRequest(request, vaccine);
        Vaccine updatedVaccine = vaccineRepository.save(vaccine);
        return vaccineMapper.toResponse(updatedVaccine);
    }
    
    @Transactional
    public void deleteVaccine(Long id) {
        log.info("Deleting vaccine with ID: {}", id);
        if (!vaccineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vaccine", "id", id);
        }
        vaccineRepository.deleteById(id);
    }
}
