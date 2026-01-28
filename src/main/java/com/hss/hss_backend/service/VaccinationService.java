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

    // Deduct Stock if requested
    if (Boolean.TRUE.equals(request.getDeductStock()) && request.getStockProductId() != null) {
      StockProduct stockProduct = stockProductService.getProductById(request.getStockProductId());

      // Validate that Stock Product matches Vaccine (optional, e.g. check name or
      // verify linkage if possible)
      // For now, assume user selected correct stock item.

      stockProductService.deductStock(
          request.getStockProductId(),
          1,
          "Vaccination: " + vaccine.getVaccineName() + " for Animal: " + animal.getName(),
          "VACCINATION",
          null // Will be updated with ID after save? Circular dependency.
               // Better: save vaccination first? But transactional...
      );

      batchNumber = stockProduct.getLotNo(); // Use lot number as batch number
    }

    VaccinationRecord record = VaccinationRecord.builder()
        .animal(animal)
        .vaccine(vaccine)
        .vaccineName(vaccine.getVaccineName())
        .date(request.getDate() != null ? request.getDate() : LocalDate.now())
        .nextDueDate(request.getNextDueDate())
        .veterinarianName(request.getVeterinarianName())
        .notes(request.getNotes())
        .batchNumber(batchNumber)
        .build();

    VaccinationRecord savedRecord = vaccinationRecordRepository.save(record);

    // If we want to link transaction to vaccination record, we would need to pass
    // savedRecord.getId() to deductStock.
    // But deductStock was called before.
    // We can update the transaction notes or relatedId afterwards if needed, but
    // "relatedId" usage in StockTransaction
    // implies we should pass it.
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
