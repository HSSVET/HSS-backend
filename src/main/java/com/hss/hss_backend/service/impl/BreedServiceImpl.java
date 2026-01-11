package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.service.BreedService;

import com.hss.hss_backend.dto.request.BreedCreateRequest;
import com.hss.hss_backend.dto.request.BreedUpdateRequest;
import com.hss.hss_backend.dto.response.BreedResponse;
import com.hss.hss_backend.entity.Breed;
import com.hss.hss_backend.entity.Species;
import com.hss.hss_backend.exception.DuplicateResourceException;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.BreedMapper;
import com.hss.hss_backend.repository.BreedRepository;
import com.hss.hss_backend.repository.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BreedServiceImpl implements BreedService {

    private final BreedRepository breedRepository;
    private final SpeciesRepository speciesRepository;

    @Override
    public BreedResponse createBreed(BreedCreateRequest request) {
        log.info("Creating breed: {} for species ID: {}", request.getName(), request.getSpeciesId());

        // Check if species exists
        Species species = speciesRepository.findById(request.getSpeciesId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Species not found with ID: " + request.getSpeciesId()));

        // Check for duplicate breed name within the same species
        if (breedRepository.findByNameAndSpeciesSpeciesId(request.getName(), request.getSpeciesId()).isPresent()) {
            throw new DuplicateResourceException(
                    "Breed with name '" + request.getName() + "' already exists for this species");
        }

        Breed breed = BreedMapper.toEntity(request, species);
        Breed savedBreed = breedRepository.save(breed);

        log.info("Successfully created breed with ID: {}", savedBreed.getBreedId());
        return BreedMapper.toResponse(savedBreed);
    }

    @Override
    @Transactional(readOnly = true)
    public BreedResponse getBreedById(Long id) {
        log.info("Fetching breed with ID: {}", id);
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Breed not found with ID: " + id));
        return BreedMapper.toResponse(breed);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BreedResponse> getAllBreeds(Pageable pageable) {
        log.info("Fetching all breeds with pagination");
        Page<Breed> breeds = breedRepository.findAll(pageable);
        return breeds.map(BreedMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BreedResponse> getBreedsBySpeciesId(Long speciesId) {
        log.info("Fetching breeds for species ID: {}", speciesId);
        List<Breed> breeds = breedRepository.findBySpeciesSpeciesId(speciesId);
        return BreedMapper.toResponseList(breeds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BreedResponse> searchBreedsByName(String name) {
        log.info("Searching breeds by name: {}", name);
        List<Breed> breeds = breedRepository.findByNameContainingIgnoreCase(name);
        return BreedMapper.toResponseList(breeds);
    }

    @Override
    public BreedResponse updateBreed(Long id, BreedUpdateRequest request) {
        log.info("Updating breed with ID: {}", id);

        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Breed not found with ID: " + id));

        // Check if species exists if it's being updated
        if (request.getSpeciesId() != null) {
            Species species = speciesRepository.findById(request.getSpeciesId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Species not found with ID: " + request.getSpeciesId()));

            // Check for duplicate breed name within the same species
            if (breedRepository.findByNameAndSpeciesSpeciesId(request.getName(), request.getSpeciesId()).isPresent()) {
                throw new DuplicateResourceException(
                        "Breed with name '" + request.getName() + "' already exists for this species");
            }

            // Update the breed's species
            breed.setSpecies(species);
        }

        BreedMapper.updateEntity(breed, request);
        Breed savedBreed = breedRepository.save(breed);

        log.info("Successfully updated breed with ID: {}", savedBreed.getBreedId());
        return BreedMapper.toResponse(savedBreed);
    }

    @Override
    public void deleteBreed(Long id) {
        log.info("Deleting breed with ID: {}", id);

        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Breed not found with ID: " + id));

        // Check if breed has associated animals
        if (!breed.getAnimals().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete breed with associated animals. Please remove all animals first.");
        }

        breedRepository.delete(breed);
        log.info("Successfully deleted breed with ID: {}", id);
    }
}
