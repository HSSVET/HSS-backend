package com.hss.hss_backend.repository;

import com.hss.hss_backend.base.BaseIntegrationTest;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Breed;
import com.hss.hss_backend.entity.Owner;
import com.hss.hss_backend.entity.Species;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AnimalRepository Integration Tests")
class AnimalRepositoryTest extends BaseIntegrationTest {

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
    @DisplayName("Should save and retrieve animal")
    void shouldSaveAndRetrieveAnimal() {
        // Given
        Animal animal = new Animal();
        animal.setName("Test Animal");
        animal.setOwner(testOwner);
        animal.setSpecies(testSpecies);
        animal.setBreed(testBreed);
        animal.setGender(Animal.Gender.MALE);
        animal.setBirthDate(LocalDate.now().minusYears(2));
        animal.setMicrochipNo("123456789012345");

        // When
        Animal saved = animalRepository.save(animal);
        Optional<Animal> found = animalRepository.findById(saved.getAnimalId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Animal");
        assertThat(found.get().getOwner().getOwnerId()).isEqualTo(testOwner.getOwnerId());
    }

    @Test
    @DisplayName("Should find animals by owner")
    void shouldFindAnimalsByOwner() {
        // Given
        Animal animal1 = createTestAnimal("Animal 1", testOwner);
        Animal animal2 = createTestAnimal("Animal 2", testOwner);
        animalRepository.save(animal1);
        animalRepository.save(animal2);

        // When
        List<Animal> animals = animalRepository.findByOwnerOwnerId(testOwner.getOwnerId());

        // Then
        assertThat(animals).hasSize(2);
        assertThat(animals).extracting(Animal::getName).containsExactlyInAnyOrder("Animal 1", "Animal 2");
    }

    @Test
    @DisplayName("Should find animals by species")
    void shouldFindAnimalsBySpecies() {
        // Given
        Animal animal1 = createTestAnimal("Animal 1", testOwner);
        Animal animal2 = createTestAnimal("Animal 2", testOwner);
        animalRepository.save(animal1);
        animalRepository.save(animal2);

        // When
        List<Animal> animals = animalRepository.findBySpeciesSpeciesId(testSpecies.getSpeciesId());

        // Then
        assertThat(animals).hasSize(2);
    }

    @Test
    @DisplayName("Should find animal by microchip number")
    void shouldFindAnimalByMicrochipNo() {
        // Given
        String microchipNo = "987654321098765";
        Animal animal = createTestAnimal("Chip Animal", testOwner);
        animal.setMicrochipNo(microchipNo);
        animalRepository.save(animal);

        // When
        Optional<Animal> found = animalRepository.findByMicrochipNo(microchipNo);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getMicrochipNo()).isEqualTo(microchipNo);
    }

    @Test
    @DisplayName("Should check if microchip exists")
    void shouldCheckIfMicrochipExists() {
        // Given
        String microchipNo = "111222333444555";
        Animal animal = createTestAnimal("Chip Animal", testOwner);
        animal.setMicrochipNo(microchipNo);
        animalRepository.save(animal);

        // When
        boolean exists = animalRepository.existsByMicrochipNo(microchipNo);
        boolean notExists = animalRepository.existsByMicrochipNo("999999999999999");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find animals by name containing")
    void shouldFindAnimalsByNameContaining() {
        // Given
        animalRepository.save(createTestAnimal("Max", testOwner));
        animalRepository.save(createTestAnimal("Maximus", testOwner));
        animalRepository.save(createTestAnimal("Charlie", testOwner));

        // When
        List<Animal> animals = animalRepository.findByNameContainingIgnoreCase("max");

        // Then
        assertThat(animals).hasSize(2);
        assertThat(animals).extracting(Animal::getName).containsExactlyInAnyOrder("Max", "Maximus");
    }

    @Test
    @DisplayName("Should paginate animals by owner")
    void shouldPaginateAnimalsByOwner() {
        // Given
        for (int i = 0; i < 15; i++) {
            animalRepository.save(createTestAnimal("Animal " + i, testOwner));
        }

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Animal> page = animalRepository.findByOwnerOwnerId(testOwner.getOwnerId(), pageable);

        // Then
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find animals by birth date range")
    void shouldFindAnimalsByBirthDateRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusYears(5);
        LocalDate endDate = LocalDate.now().minusYears(1);

        Animal youngAnimal = createTestAnimal("Young", testOwner);
        youngAnimal.setBirthDate(LocalDate.now().minusMonths(6));
        animalRepository.save(youngAnimal);

        Animal oldAnimal = createTestAnimal("Old", testOwner);
        oldAnimal.setBirthDate(LocalDate.now().minusYears(3));
        animalRepository.save(oldAnimal);

        // When
        List<Animal> animals = animalRepository.findByBirthDateBetween(startDate, endDate);

        // Then
        assertThat(animals).hasSize(1);
        assertThat(animals.get(0).getName()).isEqualTo("Old");
    }

    // Helper method
    private Animal createTestAnimal(String name, Owner owner) {
        Animal animal = new Animal();
        animal.setName(name);
        animal.setOwner(owner);
        animal.setSpecies(testSpecies);
        animal.setBreed(testBreed);
        animal.setGender(Animal.Gender.MALE);
        animal.setBirthDate(LocalDate.now().minusYears(2));
        return animal;
    }
}

