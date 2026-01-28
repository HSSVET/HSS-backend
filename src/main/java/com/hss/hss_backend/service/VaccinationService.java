package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.VaccinationCreateRequest;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Appointment;
import com.hss.hss_backend.entity.StockProduct;
import com.hss.hss_backend.entity.VaccinationRecord;
import com.hss.hss_backend.entity.Vaccine;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.VaccinationRecordRepository;
import com.hss.hss_backend.repository.VaccineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VaccinationService {

  private final VaccinationRecordRepository vaccinationRecordRepository;
  private final AnimalRepository animalRepository;
  private final VaccineRepository vaccineRepository;
  private final StockProductService stockProductService;
  private final AppointmentService appointmentService;

  public VaccinationRecord createVaccination(VaccinationCreateRequest request) {
    log.info("Creating vaccination for animal ID: {}", request.getAnimalId());

    Animal animal = animalRepository.findById(request.getAnimalId())
        .orElseThrow(() -> new ResourceNotFoundException("Animal", request.getAnimalId()));

    Vaccine vaccine = vaccineRepository.findById(request.getVaccineId())
        .orElseThrow(() -> new ResourceNotFoundException("Vaccine", request.getVaccineId()));

    String batchNumber = null;
    StockProduct usedStockProduct = null;

    // Get StockProduct if specified (for linking to vaccination record)
    if (request.getStockProductId() != null) {
      usedStockProduct = stockProductService.getProductById(request.getStockProductId());
      batchNumber = usedStockProduct.getLotNo(); // Use lot number as batch number
    }

    // Build vaccination record with StockProduct link
    VaccinationRecord record = VaccinationRecord.builder()
        .animal(animal)
        .vaccine(vaccine)
        .vaccineName(vaccine.getVaccineName())
        .date(request.getDate() != null ? request.getDate() : LocalDate.now())
        .nextDueDate(request.getNextDueDate())
        .veterinarianName(request.getVeterinarianName())
        .notes(request.getNotes())
        .batchNumber(batchNumber)
        .stockProduct(usedStockProduct)
        .clinic(animal.getOwner().getClinic())
        .build();

    VaccinationRecord savedRecord = vaccinationRecordRepository.save(record);

    // Deduct Stock after saving record (so we have the record ID)
    if (Boolean.TRUE.equals(request.getDeductStock()) && usedStockProduct != null) {
      stockProductService.deductStock(
          request.getStockProductId(),
          1,
          "Vaccination: " + vaccine.getVaccineName() + " for Animal: " + animal.getName(),
          "VACCINATION",
          savedRecord.getVaccinationRecordId()
      );
      log.info("Deducted stock for vaccination record ID: {}", savedRecord.getVaccinationRecordId());
    }
    // Link to existing Appointment and update Status
    if (request.getAppointmentId() != null) {
      appointmentService.updateAppointmentStatus(request.getAppointmentId(), Appointment.Status.COMPLETED);
    }

    // Create Next Appointment if requested
    if (Boolean.TRUE.equals(request.getCreateNextAppointment()) && request.getNextDueDate() != null) {
      String subject = "Vaccination: " + vaccine.getVaccineName();
      // Use getVeterinarianName?? No, request doesn't have ID.
      // But AppointmentService.createAppointment takes veterinarianId.
      // VaccinationCreateRequest has veterinarianName (String), but not ID.
      // We might need to resolve ID or pass null if allowed.
      // AppointmentService.createAppointment(animalId, dateTime, subject, vetId,
      // notes)

      // WARNING: request does not have veterinarianId. passing null for now.
      // If vital, we need to add veterinarianId to request.

      appointmentService.createAppointment(
          request.getAnimalId(),
          request.getNextDueDate().atTime(9, 0), // Default to 9 AM? Or just date?
          subject,
          null, // veterinarianId. Need to add to DTO if required.
          "Scheduled automatically from previous vaccination.");
    }

    return savedRecord;

  }

  public List<VaccinationRecord> getVaccinationsByAnimalId(Long animalId) {
    return vaccinationRecordRepository.findByAnimalAnimalId(animalId);
  }

  // Add other methods as needed (get due, etc)
}
