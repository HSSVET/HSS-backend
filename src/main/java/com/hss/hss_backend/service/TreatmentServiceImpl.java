package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.TreatmentCreateRequest;
import com.hss.hss_backend.dto.request.TreatmentUpdateRequest;
import com.hss.hss_backend.dto.response.TreatmentResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Clinic;
import com.hss.hss_backend.entity.Treatment;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.TreatmentMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.ClinicRepository;
import com.hss.hss_backend.repository.TreatmentRepository;
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
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final AnimalRepository animalRepository;
    private final ClinicRepository clinicRepository;

    @Override
    public TreatmentResponse createTreatment(TreatmentCreateRequest request) {
        log.info("Creating treatment for animal ID: {}", request.getAnimalId());

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal", request.getAnimalId()));

        validateClinicAccess(animal);

        Long clinicId = ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is not set");
        }

        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", clinicId));

        Treatment treatment = TreatmentMapper.toEntity(request, animal, clinic);
        Treatment savedTreatment = treatmentRepository.save(treatment);

        log.info("Treatment created successfully with ID: {}", savedTreatment.getTreatmentId());
        return TreatmentMapper.toResponse(savedTreatment);
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentResponse getTreatmentById(Long treatmentId) {
        log.info("Fetching treatment with ID: {}", treatmentId);

        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment", treatmentId));

        validateClinicAccess(treatment.getAnimal());

        return TreatmentMapper.toResponse(treatment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponse> getTreatmentsByAnimalId(Long animalId) {
        log.info("Fetching treatments for animal ID: {}", animalId);

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", animalId));

        validateClinicAccess(animal);

        List<Treatment> treatments = treatmentRepository.findByAnimal_AnimalIdOrderByStartDateDesc(animalId);
        return TreatmentMapper.toResponseList(treatments);
    }

    @Override
    public TreatmentResponse updateTreatment(Long treatmentId, TreatmentUpdateRequest request) {
        log.info("Updating treatment with ID: {}", treatmentId);

        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment", treatmentId));

        validateClinicAccess(treatment.getAnimal());

        TreatmentMapper.updateEntity(treatment, request);
        Treatment updatedTreatment = treatmentRepository.save(treatment);

        log.info("Treatment updated successfully with ID: {}", treatmentId);
        return TreatmentMapper.toResponse(updatedTreatment);
    }

    @Override
    public void deleteTreatment(Long treatmentId) {
        log.info("Deleting treatment with ID: {}", treatmentId);

        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment", treatmentId));

        validateClinicAccess(treatment.getAnimal());

        treatmentRepository.delete(treatment);
        log.info("Treatment deleted successfully with ID: {}", treatmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByAnimalId(Long animalId) {
        return treatmentRepository.countByAnimal_AnimalId(animalId);
    }

    private void validateClinicAccess(Animal animal) {
        Long currentClinicId = ClinicContext.getClinicId();
        if (currentClinicId != null && animal.getOwner().getClinic() != null) {
            if (!currentClinicId.equals(animal.getOwner().getClinic().getClinicId())) {
                throw new AccessDeniedException("You do not have permission to access this animal's treatment.");
            }
        }
    }
}
