package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.response.MedicalHistoryResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.MedicalHistory;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.MedicalHistoryMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.MedicalHistoryRepository;
import com.hss.hss_backend.security.ClinicContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final AnimalRepository animalRepository;

    @Override
    public MedicalHistoryResponse getMedicalHistoryById(Long historyId) {
        log.info("Fetching medical history with ID: {}", historyId);

        MedicalHistory medicalHistory = medicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalHistory", historyId));

        validateClinicAccess(medicalHistory.getAnimal());

        return MedicalHistoryMapper.toResponse(medicalHistory);
    }

    @Override
    public List<MedicalHistoryResponse> getMedicalHistoriesByAnimalId(Long animalId) {
        log.info("Fetching medical histories for animal ID: {}", animalId);

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", animalId));

        validateClinicAccess(animal);

        List<MedicalHistory> medicalHistories = medicalHistoryRepository.findByAnimal_AnimalIdOrderByDateDesc(animalId);
        return MedicalHistoryMapper.toResponseList(medicalHistories);
    }

    @Override
    public long countByAnimalId(Long animalId) {
        return medicalHistoryRepository.countByAnimal_AnimalId(animalId);
    }

    private void validateClinicAccess(Animal animal) {
        Long currentClinicId = ClinicContext.getClinicId();
        if (currentClinicId != null && animal.getOwner().getClinic() != null) {
            if (!currentClinicId.equals(animal.getOwner().getClinic().getClinicId())) {
                throw new AccessDeniedException("You do not have permission to access this animal's medical history.");
            }
        }
    }
}
