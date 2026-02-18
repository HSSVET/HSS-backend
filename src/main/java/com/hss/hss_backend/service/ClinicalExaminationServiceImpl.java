package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.ClinicalExaminationCreateRequest;
import com.hss.hss_backend.dto.response.ClinicalExaminationResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.ClinicalExamination;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.ClinicalExaminationMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.ClinicalExaminationRepository;
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
@Transactional
public class ClinicalExaminationServiceImpl implements ClinicalExaminationService {

    private final ClinicalExaminationRepository clinicalExaminationRepository;
    private final AnimalRepository animalRepository;

    @Override
    public ClinicalExaminationResponse createClinicalExamination(ClinicalExaminationCreateRequest request) {
        log.info("Creating clinical examination for animal ID: {}", request.getAnimalId());

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal", request.getAnimalId()));

        validateClinicAccess(animal);

        ClinicalExamination examination = ClinicalExaminationMapper.toEntity(request, animal);
        ClinicalExamination savedExamination = clinicalExaminationRepository.save(examination);

        log.info("Clinical examination created successfully with ID: {}", savedExamination.getExaminationId());
        return ClinicalExaminationMapper.toResponse(savedExamination);
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicalExaminationResponse getClinicalExaminationById(Long examinationId) {
        log.info("Fetching clinical examination with ID: {}", examinationId);

        ClinicalExamination examination = clinicalExaminationRepository.findById(examinationId)
                .orElseThrow(() -> new ResourceNotFoundException("ClinicalExamination", examinationId));

        validateClinicAccess(examination.getAnimal());

        return ClinicalExaminationMapper.toResponse(examination);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicalExaminationResponse> getClinicalExaminationsByAnimalId(Long animalId) {
        log.info("Fetching clinical examinations for animal ID: {}", animalId);

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", animalId));

        validateClinicAccess(animal);

        List<ClinicalExamination> examinations = clinicalExaminationRepository.findByAnimal_AnimalIdOrderByDateDesc(animalId);
        return ClinicalExaminationMapper.toResponseList(examinations);
    }

    @Override
    public void deleteClinicalExamination(Long examinationId) {
        log.info("Deleting clinical examination with ID: {}", examinationId);

        ClinicalExamination examination = clinicalExaminationRepository.findById(examinationId)
                .orElseThrow(() -> new ResourceNotFoundException("ClinicalExamination", examinationId));

        validateClinicAccess(examination.getAnimal());

        clinicalExaminationRepository.delete(examination);
        log.info("Clinical examination deleted successfully with ID: {}", examinationId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByAnimalId(Long animalId) {
        return clinicalExaminationRepository.countByAnimal_AnimalId(animalId);
    }

    private void validateClinicAccess(Animal animal) {
        Long currentClinicId = ClinicContext.getClinicId();
        if (currentClinicId != null && animal.getOwner().getClinic() != null) {
            if (!currentClinicId.equals(animal.getOwner().getClinic().getClinicId())) {
                throw new AccessDeniedException("You do not have permission to access this animal's clinical examinations.");
            }
        }
    }
}
