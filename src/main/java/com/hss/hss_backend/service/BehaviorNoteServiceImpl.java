package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.BehaviorNoteCreateRequest;
import com.hss.hss_backend.dto.request.BehaviorNoteUpdateRequest;
import com.hss.hss_backend.dto.response.BehaviorNoteResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.BehaviorNote;
import com.hss.hss_backend.entity.Clinic;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.BehaviorNoteMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.BehaviorNoteRepository;
import com.hss.hss_backend.repository.ClinicRepository;
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
public class BehaviorNoteServiceImpl implements BehaviorNoteService {

    private final BehaviorNoteRepository behaviorNoteRepository;
    private final AnimalRepository animalRepository;
    private final ClinicRepository clinicRepository;

    @Override
    public BehaviorNoteResponse createBehaviorNote(BehaviorNoteCreateRequest request) {
        log.info("Creating behavior note for animal ID: {}", request.getAnimalId());

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal", request.getAnimalId()));

        validateClinicAccess(animal);

        Long clinicId = ClinicContext.getClinicId();
        if (clinicId == null) {
            throw new IllegalStateException("Clinic context is not set");
        }

        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", clinicId));

        BehaviorNote behaviorNote = BehaviorNoteMapper.toEntity(request, animal, clinic);
        BehaviorNote savedBehaviorNote = behaviorNoteRepository.save(behaviorNote);

        log.info("Behavior note created successfully with ID: {}", savedBehaviorNote.getBehaviorNoteId());
        return BehaviorNoteMapper.toResponse(savedBehaviorNote);
    }

    @Override
    @Transactional(readOnly = true)
    public BehaviorNoteResponse getBehaviorNoteById(Long behaviorNoteId) {
        log.info("Fetching behavior note with ID: {}", behaviorNoteId);

        BehaviorNote behaviorNote = behaviorNoteRepository.findById(behaviorNoteId)
                .orElseThrow(() -> new ResourceNotFoundException("BehaviorNote", behaviorNoteId));

        validateClinicAccess(behaviorNote.getAnimal());

        return BehaviorNoteMapper.toResponse(behaviorNote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BehaviorNoteResponse> getBehaviorNotesByAnimalId(Long animalId) {
        log.info("Fetching behavior notes for animal ID: {}", animalId);

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", animalId));

        validateClinicAccess(animal);

        List<BehaviorNote> behaviorNotes = behaviorNoteRepository.findByAnimal_AnimalIdOrderByObservedDateDesc(animalId);
        return BehaviorNoteMapper.toResponseList(behaviorNotes);
    }

    @Override
    public BehaviorNoteResponse updateBehaviorNote(Long behaviorNoteId, BehaviorNoteUpdateRequest request) {
        log.info("Updating behavior note with ID: {}", behaviorNoteId);

        BehaviorNote behaviorNote = behaviorNoteRepository.findById(behaviorNoteId)
                .orElseThrow(() -> new ResourceNotFoundException("BehaviorNote", behaviorNoteId));

        validateClinicAccess(behaviorNote.getAnimal());

        BehaviorNoteMapper.updateEntity(behaviorNote, request);
        BehaviorNote updatedBehaviorNote = behaviorNoteRepository.save(behaviorNote);

        log.info("Behavior note updated successfully with ID: {}", behaviorNoteId);
        return BehaviorNoteMapper.toResponse(updatedBehaviorNote);
    }

    @Override
    public void deleteBehaviorNote(Long behaviorNoteId) {
        log.info("Deleting behavior note with ID: {}", behaviorNoteId);

        BehaviorNote behaviorNote = behaviorNoteRepository.findById(behaviorNoteId)
                .orElseThrow(() -> new ResourceNotFoundException("BehaviorNote", behaviorNoteId));

        validateClinicAccess(behaviorNote.getAnimal());

        behaviorNoteRepository.delete(behaviorNote);
        log.info("Behavior note deleted successfully with ID: {}", behaviorNoteId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByAnimalId(Long animalId) {
        return behaviorNoteRepository.countByAnimal_AnimalId(animalId);
    }

    private void validateClinicAccess(Animal animal) {
        Long currentClinicId = ClinicContext.getClinicId();
        if (currentClinicId != null && animal.getOwner().getClinic() != null) {
            if (!currentClinicId.equals(animal.getOwner().getClinic().getClinicId())) {
                throw new AccessDeniedException("You do not have permission to access this animal's behavior notes.");
            }
        }
    }
}
