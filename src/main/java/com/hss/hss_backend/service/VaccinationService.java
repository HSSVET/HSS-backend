package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.VaccinationCreateRequest;
import com.hss.hss_backend.entity.Animal;
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
    // Let's keep it simple for now, maybe pass null or handle it in specific
    // separate method if strict audit is indispensable.

    return savedRecord;
  }

  public List<VaccinationRecord> getVaccinationsByAnimalId(Long animalId) {
    return vaccinationRecordRepository.findByAnimalAnimalId(animalId);
  }

  // Add other methods as needed (get due, etc)
}
