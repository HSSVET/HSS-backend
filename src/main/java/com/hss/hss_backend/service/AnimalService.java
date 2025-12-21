package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.AnimalCreateRequest;
import com.hss.hss_backend.dto.request.AnimalUpdateRequest;
import com.hss.hss_backend.dto.response.AnimalDetailResponse;
import com.hss.hss_backend.dto.response.AnimalResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Breed;
import com.hss.hss_backend.entity.Owner;
import com.hss.hss_backend.entity.Species;
import com.hss.hss_backend.exception.DuplicateResourceException;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.AnimalMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.BreedRepository;
import com.hss.hss_backend.repository.OwnerRepository;
import com.hss.hss_backend.repository.SpeciesRepository;
import com.hss.hss_backend.security.ClinicContext; // Added import
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException; // Added import

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;

    // Helper to validate clinic access
    private void validateClinicAccess(Animal animal) {
        Long currentClinicId = ClinicContext.getClinicId();
        if (currentClinicId != null && animal.getOwner().getClinic() != null) {
            if (!currentClinicId.equals(animal.getOwner().getClinic().getClinicId())) {
                throw new AccessDeniedException("You do not have permission to access this animal.");
            }
        }
    }

    public AnimalResponse createAnimal(AnimalCreateRequest request) {
        log.info("Creating animal with name: {}", request.getName());

        // Validate owner exists
        Owner owner = ownerRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner", request.getOwnerId()));

        // Validate species exists
        Species species = speciesRepository.findById(request.getSpeciesId())
                .orElseThrow(() -> new ResourceNotFoundException("Species", request.getSpeciesId()));

        // Validate breed exists and belongs to species
        Breed breed = breedRepository.findById(request.getBreedId())
                .orElseThrow(() -> new ResourceNotFoundException("Breed", request.getBreedId()));

        if (!breed.getSpecies().getSpeciesId().equals(request.getSpeciesId())) {
            throw new IllegalArgumentException("Breed does not belong to the specified species");
        }

        // Check for duplicate microchip number
        if (request.getMicrochipNo() != null && !request.getMicrochipNo().trim().isEmpty()) {
            if (animalRepository.existsByMicrochipNo(request.getMicrochipNo())) {
                throw new DuplicateResourceException("Animal", "microchipNo", request.getMicrochipNo());
            }
        }

        Animal animal = AnimalMapper.toEntity(request, owner, species, breed);
        Animal savedAnimal = animalRepository.save(animal);

        log.info("Animal created successfully with ID: {}", savedAnimal.getAnimalId());
        return AnimalMapper.toResponse(savedAnimal);
    }

    @Transactional(readOnly = true)
    public AnimalResponse getAnimalById(Long id) {
        log.info("Fetching animal with ID: {}", id);
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        validateClinicAccess(animal); // Validate access
        return AnimalMapper.toResponse(animal);
    }

    @Transactional(readOnly = true)
    public AnimalDetailResponse getAnimalDetailById(Long id) {
        log.info("Fetching animal detail with ID: {}", id);
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        validateClinicAccess(animal); // Validate access
        return AnimalMapper.toDetailResponse(animal);
    }

    @Transactional(readOnly = true)
    public Page<AnimalResponse> getAllAnimals(Pageable pageable) {
        log.info("Fetching all animals with pagination");
        Long clinicId = ClinicContext.getClinicId();
        Page<Animal> animals;

        if (clinicId != null) {
            animals = animalRepository.findByOwnerClinicClinicId(clinicId, pageable);
        } else {
            // Fallback for super admin or non-clinic context (if any)
            animals = animalRepository.findAll(pageable);
        }
        return animals.map(AnimalMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> getAnimalsByOwnerId(Long ownerId) {
        log.info("Fetching animals for owner ID: {}", ownerId);
        List<Animal> animals = animalRepository.findByOwnerOwnerId(ownerId);
        return AnimalMapper.toResponseList(animals);
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> searchAnimalsByName(String name) {
        log.info("Searching animals by name: {}", name);
        List<Animal> animals = animalRepository.findByNameContainingIgnoreCase(name);
        return AnimalMapper.toResponseList(animals);
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> searchAnimalsByOwnerName(String ownerName) {
        log.info("Searching animals by owner name: {}", ownerName);
        List<Animal> animals = animalRepository.findByOwnerNameContaining(ownerName);
        return AnimalMapper.toResponseList(animals);
    }

    @Transactional(readOnly = true)
    public AnimalResponse getAnimalByMicrochip(String microchipNo) {
        log.info("Fetching animal by microchip: {}", microchipNo);
        Animal animal = animalRepository.findByMicrochipNo(microchipNo)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", "microchipNo", microchipNo));
        return AnimalMapper.toResponse(animal);
    }

    public AnimalResponse updateAnimal(Long id, AnimalUpdateRequest request) {
        log.info("Updating animal with ID: {}", id);
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        validateClinicAccess(animal); // Validate access

        // Validate species exists
        Species species = speciesRepository.findById(request.getSpeciesId())
                .orElseThrow(() -> new ResourceNotFoundException("Species", request.getSpeciesId()));

        // Validate breed exists and belongs to species
        Breed breed = breedRepository.findById(request.getBreedId())
                .orElseThrow(() -> new ResourceNotFoundException("Breed", request.getBreedId()));

        if (!breed.getSpecies().getSpeciesId().equals(request.getSpeciesId())) {
            throw new IllegalArgumentException("Breed does not belong to the specified species");
        }

        // Check for duplicate microchip number (excluding current animal)
        if (request.getMicrochipNo() != null && !request.getMicrochipNo().trim().isEmpty()) {
            if (animalRepository.existsByMicrochipNo(request.getMicrochipNo()) &&
                    !request.getMicrochipNo().equals(animal.getMicrochipNo())) {
                throw new DuplicateResourceException("Animal", "microchipNo", request.getMicrochipNo());
            }
        }

        AnimalMapper.updateEntity(animal, request, species, breed);
        Animal updatedAnimal = animalRepository.save(animal);

        log.info("Animal updated successfully with ID: {}", updatedAnimal.getAnimalId());
        return AnimalMapper.toResponse(updatedAnimal);
    }

    public void deleteAnimal(Long id) {
        log.info("Deleting animal with ID: {}", id);
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        validateClinicAccess(animal); // Validate access
        animalRepository.delete(animal);
        log.info("Animal deleted successfully with ID: {}", id);
    }
}
