package com.hss.hss_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hss.hss_backend.base.BaseIntegrationTest;
import com.hss.hss_backend.dto.request.AnimalCreateRequest;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Breed;
import com.hss.hss_backend.entity.Owner;
import com.hss.hss_backend.entity.Species;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.BreedRepository;
import com.hss.hss_backend.repository.OwnerRepository;
import com.hss.hss_backend.repository.SpeciesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("AnimalController Integration Tests")
class AnimalControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("Should create animal via API")
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateAnimalViaAPI() throws Exception {
        // Given
        AnimalCreateRequest request = new AnimalCreateRequest();
        request.setName("API Test Animal");
        request.setOwnerId(testOwner.getOwnerId());
        request.setSpeciesId(testSpecies.getSpeciesId());
        request.setBreedId(testBreed.getBreedId());
        request.setGender("MALE");
        request.setBirthDate(LocalDate.now().minusYears(2));

        // When/Then
        mockMvc.perform(post("/api/animals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("API Test Animal"))
                .andExpect(jsonPath("$.ownerId").value(testOwner.getOwnerId()));
    }

    @Test
    @DisplayName("Should get animal by id via API")
    @WithMockUser(roles = {"ADMIN"})
    void shouldGetAnimalByIdViaAPI() throws Exception {
        // Given
        Animal animal = createTestAnimal();
        animal = animalRepository.save(animal);

        // When/Then
        mockMvc.perform(get("/api/animals/{id}", animal.getAnimalId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalId").value(animal.getAnimalId()))
                .andExpect(jsonPath("$.name").value(animal.getName()));
    }

    @Test
    @DisplayName("Should get all animals with pagination via API")
    @WithMockUser(roles = {"ADMIN"})
    void shouldGetAllAnimalsWithPaginationViaAPI() throws Exception {
        // Given
        for (int i = 0; i < 5; i++) {
            Animal animal = createTestAnimal();
            animal.setName("Animal " + i);
            animalRepository.save(animal);
        }

        // When/Then
        mockMvc.perform(get("/api/animals")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(5));
    }

    @Test
    @DisplayName("Should return 404 when animal not found")
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn404WhenAnimalNotFound() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/animals/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 403 when unauthorized")
    void shouldReturn403WhenUnauthorized() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/animals"))
                .andExpect(status().isForbidden());
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

