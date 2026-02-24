package com.hss.hss_backend.service;

import com.hss.hss_backend.base.BaseIntegrationTest;
import com.hss.hss_backend.dto.request.AnimalCreateRequest;
import com.hss.hss_backend.dto.request.AnimalUpdateRequest;
import com.hss.hss_backend.dto.response.AnimalResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Breed;
import com.hss.hss_backend.entity.Owner;
import com.hss.hss_backend.entity.Species;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.BreedRepository;
import com.hss.hss_backend.repository.OwnerRepository;
import com.hss.hss_backend.repository.SpeciesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AnimalService Integration Tests")
class AnimalServiceTest extends BaseIntegrationTest {

    @Autowired
    private AnimalService animalService;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private SpeciesRepository speciesRepository;

    @Autowired
    private BreedRepository breedRepository;

    private Owner testOwner;
    private Species testSpecies;
    private Breed testBreed;

    @BeforeEach
    protected void setUp() {
        // Create test data
        testOwner = new Owner();
        testOwner.setFirstName("Test");
        testOwner.setLastName("Owner");
        testOwner.setEmail("test@example.com");
        testOwner.setPhone("5551234567");
        testOwner = ownerRepository.save(testOwner);

        testSpecies = new Species();
        testSpecies.setName("Kedi");
        testSpecies = speciesRepository.save(testSpecies);

        testBreed = new Breed();
        testBreed.setName("Tekir");
        testBreed.setSpecies(testSpecies);
        testBreed = breedRepository.save(testBreed);
    }

    @Test
    @DisplayName("Should create animal successfully")
    void shouldCreateAnimal() {
        // Given
        AnimalCreateRequest request = new AnimalCreateRequest();
        request.setName("Test Animal");
        request.setOwnerId(testOwner.getOwnerId());
        request.setSpeciesId(testSpecies.getSpeciesId());
        request.setBreedId(testBreed.getBreedId());
        request.setGender("MALE");
        request.setBirthDate(LocalDate.now().minusYears(2));
        request.setMicrochipNo("123456789012345");

        // When
        AnimalResponse response = animalService.createAnimal(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Animal");
        assertThat(response.getOwnerId()).isEqualTo(testOwner.getOwnerId());
        
        // Verify in database
        Animal saved = animalRepository.findById(response.getAnimalId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Test Animal");
    }

    @Test
    @DisplayName("Should throw exception when owner not found")
    void shouldThrowExceptionWhenOwnerNotFound() {
        // Given
        AnimalCreateRequest request = new AnimalCreateRequest();
        request.setName("Test Animal");
        request.setOwnerId(99999L); // Non-existent owner
        request.setSpeciesId(testSpecies.getSpeciesId());
        request.setBreedId(testBreed.getBreedId());
        request.setGender("MALE");
        request.setBirthDate(LocalDate.now().minusYears(2));

        // When/Then
        assertThatThrownBy(() -> animalService.createAnimal(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Owner");
    }

    @Test
    @DisplayName("Should get animal by id")
    void shouldGetAnimalById() {
        // Given
        Animal animal = createTestAnimal();
        animal = animalRepository.save(animal);

        // When
        AnimalResponse response = animalService.getAnimalById(animal.getAnimalId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAnimalId()).isEqualTo(animal.getAnimalId());
        assertThat(response.getName()).isEqualTo(animal.getName());
    }

    @Test
    @DisplayName("Should throw exception when animal not found")
    void shouldThrowExceptionWhenAnimalNotFound() {
        // When/Then
        assertThatThrownBy(() -> animalService.getAnimalById(99999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Animal");
    }

    @Test
    @DisplayName("Should update animal successfully")
    void shouldUpdateAnimal() {
        // Given
        Animal animal = createTestAnimal();
        animal = animalRepository.save(animal);

        AnimalUpdateRequest updateRequest = new AnimalUpdateRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setWeight(java.math.BigDecimal.valueOf(15.5));

        // When
        AnimalResponse response = animalService.updateAnimal(animal.getAnimalId(), updateRequest);

        // Then
        assertThat(response.getName()).isEqualTo("Updated Name");
        assertThat(response.getWeight()).isEqualByComparingTo(java.math.BigDecimal.valueOf(15.5));
        
        // Verify in database
        Animal updated = animalRepository.findById(animal.getAnimalId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("Should get all animals with pagination")
    void shouldGetAllAnimalsWithPagination() {
        // Given
        for (int i = 0; i < 15; i++) {
            Animal animal = createTestAnimal();
            animal.setName("Animal " + i);
            animalRepository.save(animal);
        }

        // When
        Page<AnimalResponse> page = animalService.getAllAnimals(PageRequest.of(0, 10), null);

        // Then
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should delete animal successfully")
    void shouldDeleteAnimal() {
        // Given
        Animal animal = createTestAnimal();
        animal = animalRepository.save(animal);
        Long animalId = animal.getAnimalId();

        // When
        animalService.deleteAnimal(animalId);

        // Then
        assertThat(animalRepository.findById(animalId)).isEmpty();
    }

    // Helper method
    private Animal createTestAnimal() {
        Animal animal = new Animal();
        animal.setName("Test Animal");
        animal.setOwner(testOwner);
        animal.setSpecies(testSpecies);
        animal.setBreed(testBreed);
        animal.setGender(Animal.Gender.MALE);
        animal.setBirthDate(LocalDate.now().minusYears(2));
        return animal;
    }
}

