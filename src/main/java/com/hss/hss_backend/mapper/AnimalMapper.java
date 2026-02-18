package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.request.AnimalCreateRequest;
import com.hss.hss_backend.dto.request.AnimalUpdateRequest;
import com.hss.hss_backend.dto.response.AnimalDetailResponse;
import com.hss.hss_backend.dto.response.AnimalResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Breed;
import com.hss.hss_backend.entity.Owner;
import com.hss.hss_backend.entity.Species;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AnimalMapper {

    public static Animal toEntity(AnimalCreateRequest request, Owner owner, Species species, Breed breed) {
        return Animal.builder()
                .owner(owner)
                .name(request.getName())
                .species(species)
                .breed(breed)
                .gender(request.getGender() != null ? Animal.Gender.valueOf(request.getGender().toUpperCase()) : null)
                .birthDate(request.getBirthDate())
                .weight(request.getWeight())
                .color(request.getColor())
                .microchipNo(request.getMicrochipNo())
                .allergies(request.getAllergies())
                .chronicDiseases(request.getChronicDiseases())
                .notes(request.getNotes())
                .status(request.getStatus() != null ? Animal.AnimalStatus.valueOf(request.getStatus())
                        : Animal.AnimalStatus.ACTIVE)
                .behaviorNotes(request.getBehaviorNotes())
                .profileImageUrl(request.getProfileImageUrl())
                .height(request.getHeight())
                .sterilized(request.getSterilized())
                .build();
    }

    public static void updateEntity(Animal animal, AnimalUpdateRequest request, Species species, Breed breed) {
        animal.setName(request.getName());
        animal.setSpecies(species);
        animal.setBreed(breed);
        animal.setGender(request.getGender() != null ? Animal.Gender.valueOf(request.getGender().toUpperCase()) : null);
        animal.setBirthDate(request.getBirthDate());
        animal.setWeight(request.getWeight());
        animal.setColor(request.getColor());
        animal.setMicrochipNo(request.getMicrochipNo());
        animal.setAllergies(request.getAllergies());
        animal.setChronicDiseases(request.getChronicDiseases());
        animal.setNotes(request.getNotes());
        if (request.getStatus() != null) {
            animal.setStatus(Animal.AnimalStatus.valueOf(request.getStatus()));
        }
        animal.setBehaviorNotes(request.getBehaviorNotes());
        animal.setProfileImageUrl(request.getProfileImageUrl());
        animal.setHeight(request.getHeight());
        animal.setSterilized(request.getSterilized());
    }

    public static AnimalResponse toResponse(Animal animal) {
        return AnimalResponse.builder()
                .animalId(animal.getAnimalId())
                .ownerId(animal.getOwner().getOwnerId())
                .ownerName(animal.getOwner().getFirstName() + " " + animal.getOwner().getLastName())
                .name(animal.getName())
                .speciesId(animal.getSpecies().getSpeciesId())
                .speciesName(animal.getSpecies().getName())
                .breedId(animal.getBreed().getBreedId())
                .breedName(animal.getBreed().getName())
                .gender(animal.getGender() != null ? animal.getGender().name() : null)
                .birthDate(animal.getBirthDate())
                .weight(animal.getWeight())
                .color(animal.getColor())
                .microchipNo(animal.getMicrochipNo())
                .allergies(animal.getAllergies())
                .chronicDiseases(animal.getChronicDiseases())
                .notes(animal.getNotes())
                .status(animal.getStatus() != null ? animal.getStatus().name() : null)
                .behaviorNotes(animal.getBehaviorNotes())
                .profileImageUrl(animal.getProfileImageUrl())
                .createdAt(animal.getCreatedAt())
                .updatedAt(animal.getUpdatedAt())
                .height(animal.getHeight())
                .sterilized(animal.getSterilized())
                .conditions(animal.getConditions() != null ? animal.getConditions().stream()
                        .map(c -> com.hss.hss_backend.dto.AnimalConditionDTO.builder()
                                .id(c.getId())
                                .animalId(c.getAnimal().getAnimalId())
                                .type(c.getType().name())
                                .name(c.getName())
                                .severity(c.getSeverity() != null ? c.getSeverity().name() : null)
                                .diagnosisDate(c.getDiagnosisDate())
                                .diagnosedBy(c.getDiagnosedBy())
                                .status(c.getStatus() != null ? c.getStatus().name() : null)
                                .notes(c.getNotes())
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public static AnimalDetailResponse toDetailResponse(Animal animal) {
        return AnimalDetailResponse.builder()
                .animalId(animal.getAnimalId())
                .ownerId(animal.getOwner().getOwnerId())
                .ownerName(animal.getOwner().getFirstName() + " " + animal.getOwner().getLastName())
                .ownerPhone(animal.getOwner().getPhone())
                .ownerEmail(animal.getOwner().getEmail())
                .name(animal.getName())
                .speciesId(animal.getSpecies().getSpeciesId())
                .speciesName(animal.getSpecies().getName())
                .breedId(animal.getBreed().getBreedId())
                .breedName(animal.getBreed().getName())
                .gender(animal.getGender() != null ? animal.getGender().name() : null)
                .birthDate(animal.getBirthDate())
                .weight(animal.getWeight())
                .color(animal.getColor())
                .microchipNo(animal.getMicrochipNo())
                .allergies(animal.getAllergies())
                .chronicDiseases(animal.getChronicDiseases())
                .notes(animal.getNotes())
                .status(animal.getStatus() != null ? animal.getStatus().name() : null)
                .behaviorNotes(animal.getBehaviorNotes())
                .profileImageUrl(animal.getProfileImageUrl())
                .createdAt(animal.getCreatedAt())
                .updatedAt(animal.getUpdatedAt())
                .height(animal.getHeight())
                .sterilized(animal.getSterilized())
                .conditions(animal.getConditions() != null ? animal.getConditions().stream()
                        .map(c -> com.hss.hss_backend.dto.AnimalConditionDTO.builder()
                                .id(c.getId())
                                .animalId(c.getAnimal().getAnimalId())
                                .type(c.getType().name())
                                .name(c.getName())
                                .severity(c.getSeverity() != null ? c.getSeverity().name() : null)
                                .diagnosisDate(c.getDiagnosisDate())
                                .diagnosedBy(c.getDiagnosedBy())
                                .status(c.getStatus() != null ? c.getStatus().name() : null)
                                .notes(c.getNotes())
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public static List<AnimalResponse> toResponseList(List<Animal> animals) {
        return animals.stream()
                .map(AnimalMapper::toResponse)
                .collect(Collectors.toList());
    }
}
