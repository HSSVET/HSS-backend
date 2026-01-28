package com.hss.hss_backend.integration;

import com.hss.hss_backend.dto.request.VaccinationCreateRequest;
import com.hss.hss_backend.entity.*;
import com.hss.hss_backend.repository.*;
import com.hss.hss_backend.service.VaccinationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class VaccinationFlowIntegrationTest {

  @Autowired
  private VaccinationService vaccinationService;

  @Autowired
  private AnimalRepository animalRepository;

  @Autowired
  private OwnerRepository ownerRepository;

  @Autowired
  private VaccineRepository vaccineRepository;

  @Autowired
  private StockProductRepository stockProductRepository;

  @Autowired
  private AppointmentRepository appointmentRepository;

  @Autowired
  private ClinicRepository clinicRepository;

  @Autowired
  private SpeciesRepository speciesRepository;

  @Autowired
  private BreedRepository breedRepository;

  // TODO: Autowire InvoiceRepository if we are testing that too
  // @Autowired
  // private InvoiceRepository invoiceRepository;

  private Animal animal;
  private Vaccine vaccine;
  private StockProduct stockProduct;
  private Appointment appointment;

  @BeforeEach
  void setUp() {
    // Setup Clinic
    Clinic clinic = new Clinic();
    clinic.setName("Test Clinic");
    clinic.setAddress("123 Test St");
    clinic.setPhone("555-1234");
    clinic.setEmail("clinic@test.com");
    clinicRepository.save(clinic);

    // Setup Owner & Animal
    Owner owner = new Owner();
    owner.setFirstName("John");
    owner.setLastName("Doe");
    owner.setEmail("john.doe@example.com");
    owner.setClinic(clinic);
    ownerRepository.save(owner);

    // Setup Species & Breed
    Species species = new Species();
    species.setName("Dog");
    speciesRepository.save(species);

    Breed breed = new Breed();
    breed.setName("Golden Retriever");
    breed.setSpecies(species);
    breedRepository.save(breed);

    animal = new Animal();
    animal.setName("Buddy");
    animal.setOwner(owner);
    animal.setClinic(clinic); // Set Clinic
    animal.setSpecies(species); // Set Species
    animal.setBreed(breed);
    animalRepository.save(animal);

    // Setup Stock & Vaccine
    stockProduct = new StockProduct();
    stockProduct.setName("Rabies Vaccine Batch A");
    stockProduct.setBarcode("VAC-RAB-001");
    stockProduct.setCurrentStock(10);
    stockProduct.setLotNo("LOT123");
    stockProductRepository.save(stockProduct);

    vaccine = new Vaccine();
    vaccine.setVaccineName("Rabies");
    vaccine.setNotes("Anti-Rabies");
    vaccineRepository.save(vaccine);

    // Setup Appointment
    appointment = new Appointment();
    appointment.setAnimal(animal);
    appointment.setDateTime(LocalDateTime.now().plusDays(-1));
    appointment.setStatus(Appointment.Status.SCHEDULED);
    appointment.setClinic(clinic); // Set Clinic
    appointment.setVeterinarianId(1L);
    appointmentRepository.save(appointment);
  }

  @Test
  void testVaccinationFlow_ShouldDeductStockAndCompleteAppointment() {
    // Given
    VaccinationCreateRequest request = new VaccinationCreateRequest();
    request.setAnimalId(animal.getAnimalId());
    request.setVaccineId(vaccine.getVaccineId());
    request.setVeterinarianName("Dr. Vet");
    request.setDate(LocalDate.now());
    request.setNextDueDate(LocalDate.now().plusYears(1));

    // New fields to be implemented
    request.setDeductStock(true);
    request.setStockProductId(stockProduct.getProductId());
    request.setAppointmentId(appointment.getAppointmentId());
    request.setCreateNextAppointment(true);

    // When
    vaccinationService.createVaccination(request);

    // Then
    // 1. Verify Stock Deduction
    StockProduct updatedStock = stockProductRepository.findById(stockProduct.getProductId()).orElseThrow();
    assertEquals(9, updatedStock.getCurrentStock(), "Stock should be reduced by 1");

    // 2. Verify Appointment Status Updated to COMPLETED
    Appointment updatedAppointment = appointmentRepository.findById(appointment.getAppointmentId()).orElseThrow();
    assertEquals(Appointment.Status.COMPLETED, updatedAppointment.getStatus(),
        "Appointment status should be COMPLETED");

    // 3. Verify Next Appointment Created
    long upcomingCount = appointmentRepository.findAll().stream()
        .filter(a -> a.getAnimal().equals(animal)
            && a.getStatus() == Appointment.Status.SCHEDULED
            && a.getSubject().contains("Vaccination"))
        .count();
    assertTrue(upcomingCount > 0, "A new appointment should be created for the next due date");
  }
}
