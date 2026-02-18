package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.dto.SurgeryDto;
import com.hss.hss_backend.dto.SurgeryMedicationDto;
import com.hss.hss_backend.dto.request.ConsentFormRequest;
import com.hss.hss_backend.dto.request.PreOpChecklistRequest;
import com.hss.hss_backend.dto.request.SurgeryCompleteRequest;
import com.hss.hss_backend.entity.*;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.SurgeryMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.AppointmentRepository;
import com.hss.hss_backend.repository.ConsentFormRepository;
import com.hss.hss_backend.repository.HospitalizationRepository;
import com.hss.hss_backend.repository.SurgeryRepository;
import com.hss.hss_backend.service.SmsService;
import com.hss.hss_backend.service.SurgeryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SurgeryServiceImpl implements SurgeryService {

  private final SurgeryRepository surgeryRepository;
  private final AnimalRepository animalRepository;
  private final AppointmentRepository appointmentRepository;
  private final ConsentFormRepository consentFormRepository;
  private final HospitalizationRepository hospitalizationRepository;
  private final SurgeryMapper surgeryMapper;
  private final com.hss.hss_backend.service.StockProductService stockProductService;
  private final SmsService smsService;

  @Override
  public SurgeryDto createSurgery(SurgeryDto surgeryDto) {
    Animal animal = animalRepository.findById(surgeryDto.getAnimalId())
        .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + surgeryDto.getAnimalId()));

    Surgery surgery = surgeryMapper.toEntity(surgeryDto);
    surgery.setAnimal(animal);
    surgery.setStatus("PLANNED");

    Surgery savedSurgery = surgeryRepository.save(surgery);
    return surgeryMapper.toDto(savedSurgery);
  }

  @Override
  public SurgeryDto getSurgeryById(Long id) {
    Surgery surgery = surgeryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + id));
    return surgeryMapper.toDto(surgery);
  }

  @Override
  public List<SurgeryDto> getSurgeriesByAnimalId(Long animalId) {
    List<Surgery> surgeries = surgeryRepository.findByAnimal_AnimalId(animalId);
    return surgeries.stream().map(surgeryMapper::toDto).toList();
  }

  @Override
  public SurgeryDto updateSurgeryStatus(Long id, String status) {
    Surgery surgery = surgeryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + id));

    surgery.setStatus(status);
    Surgery updatedSurgery = surgeryRepository.save(surgery);
    return surgeryMapper.toDto(updatedSurgery);
  }

  @Override
  public SurgeryDto updateSurgeryDetails(Long id, SurgeryDto surgeryDto) {
    Surgery surgery = surgeryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + id));

    if (surgeryDto.getDate() != null)
      surgery.setDate(surgeryDto.getDate());
    if (surgeryDto.getNotes() != null)
      surgery.setNotes(surgeryDto.getNotes());
    if (surgeryDto.getPreOpInstructions() != null)
      surgery.setPreOpInstructions(surgeryDto.getPreOpInstructions());
    if (surgeryDto.getPostOpInstructions() != null)
      surgery.setPostOpInstructions(surgeryDto.getPostOpInstructions());
    if (surgeryDto.getAnesthesiaProtocol() != null)
      surgery.setAnesthesiaProtocol(surgeryDto.getAnesthesiaProtocol());
    if (surgeryDto.getAnesthesiaConsent() != null)
      surgery.setAnesthesiaConsent(surgeryDto.getAnesthesiaConsent());
    if (surgeryDto.getVeterinarianId() != null)
      surgery.setVeterinarianId(surgeryDto.getVeterinarianId());
    if (surgeryDto.getStatus() != null)
      surgery.setStatus(surgeryDto.getStatus());

    Surgery updatedSurgery = surgeryRepository.save(surgery);
    return surgeryMapper.toDto(updatedSurgery);
  }

  @Override
  public SurgeryDto addMedication(Long surgeryId, SurgeryMedicationDto medicationDto) {
    Surgery surgery = surgeryRepository.findById(surgeryId)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + surgeryId));

    SurgeryMedication med = surgeryMapper.toMedicationEntity(medicationDto);
    med.setSurgery(surgery);

    surgery.getMedications().add(med);

    // Deduct stock if stockProductId is provided (assuming DTO has it or using
    // medicineId as fallback)
    Long stockId = medicationDto.getMedicineId(); // Using medicineId as stockId for simplicity if mapped
    if (stockId != null) {
      stockProductService.deductStock(stockId,
          medicationDto.getQuantity() != null ? medicationDto.getQuantity().intValue() : 1,
          "Surgery Medication: SURGERY for Animal: " + surgery.getAnimal().getName(),
          "SURGERY",
          surgery.getSurgeryId());
    }

    return surgeryMapper.toDto(surgeryRepository.save(surgery));
  }

  @Override
  public void removeMedication(Long surgeryMedId) {
    // Logic to remove medication and potentially restore stock
    // Implementation requires Repository for SurgeryMedication or cascade removal
    // via Surgery parent
  }

  @Override
  public SurgeryDto updatePreOpChecklist(Long surgeryId, PreOpChecklistRequest request) {
    log.info("Updating pre-op checklist for surgery ID: {}", surgeryId);
    
    Surgery surgery = surgeryRepository.findById(surgeryId)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + surgeryId));

    if (request.getPreOpExamCompleted() != null) {
      surgery.setPreOpExamCompleted(request.getPreOpExamCompleted());
      if (request.getPreOpExamCompleted()) {
        surgery.setPreOpExamDate(LocalDateTime.now());
      }
    }
    
    if (request.getPreOpTestsCompleted() != null) {
      surgery.setPreOpTestsCompleted(request.getPreOpTestsCompleted());
    }
    
    if (request.getRequiredTests() != null) {
      surgery.setRequiredTests(request.getRequiredTests());
    }
    
    if (request.getFastingHours() != null) {
      surgery.setFastingHours(request.getFastingHours());
    }
    
    if (request.getNotes() != null) {
      String existingNotes = surgery.getNotes() != null ? surgery.getNotes() + "\n" : "";
      surgery.setNotes(existingNotes + "Pre-op checklist: " + request.getNotes());
    }

    Surgery updated = surgeryRepository.save(surgery);
    log.info("Pre-op checklist updated for surgery ID: {}", surgeryId);
    
    return surgeryMapper.toDto(updated);
  }

  @Override
  public SurgeryDto recordConsentForm(Long surgeryId, ConsentFormRequest request) {
    log.info("Recording consent form for surgery ID: {}, type: {}", surgeryId, request.getFormType());
    
    Surgery surgery = surgeryRepository.findById(surgeryId)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + surgeryId));

    // Create and save consent form entity
    ConsentForm consentForm = ConsentForm.builder()
        .clinic(surgery.getClinic())
        .owner(surgery.getAnimal().getOwner())
        .animal(surgery.getAnimal())
        .surgery(surgery)
        .formType(ConsentForm.FormType.valueOf(request.getFormType()))
        .formTitle(request.getFormType() + " Consent Form")
        .formContent("Consent recorded via workflow")
        .signatureData(request.getSignatureData())
        .signatureDate(LocalDateTime.now())
        .signerName(request.getSignerName())
        .signerRelation(request.getSignerRelation())
        .witnessName(request.getWitnessName())
        .status(ConsentForm.ConsentStatus.SIGNED)
        .notes(request.getNotes())
        .build();

    consentFormRepository.save(consentForm);

    // Update surgery consent tracking fields
    if ("ANESTHESIA".equals(request.getFormType())) {
      surgery.setAnesthesiaConsentSigned(true);
      surgery.setAnesthesiaConsentDate(LocalDateTime.now());
    } else if ("SURGERY".equals(request.getFormType())) {
      surgery.setSurgeryConsentSigned(true);
      surgery.setSurgeryConsentDate(LocalDateTime.now());
    }

    Surgery updated = surgeryRepository.save(surgery);
    log.info("Consent form recorded for surgery ID: {}", surgeryId);
    
    return surgeryMapper.toDto(updated);
  }

  @Override
  public SurgeryDto completeSurgery(Long surgeryId, SurgeryCompleteRequest request) {
    log.info("Completing surgery ID: {}", surgeryId);
    
    Surgery surgery = surgeryRepository.findById(surgeryId)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + surgeryId));

    // Update surgery status and details
    surgery.setStatus("COMPLETED");
    surgery.setActualEndTime(LocalDateTime.now());
    surgery.setDischargeType(request.getDischargeType());
    surgery.setDischargeDate(LocalDateTime.now());

    if (request.getPostOpNotes() != null) {
      surgery.setPostOpInstructions(request.getPostOpNotes());
    }
    
    if (request.getComplications() != null) {
      surgery.setComplications(request.getComplications());
    }
    
    if (request.getPrescriptionId() != null) {
      surgery.setPrescriptionId(request.getPrescriptionId());
    }

    // Handle follow-up appointment
    if (request.getFollowUpAppointmentId() != null) {
      Appointment followUp = appointmentRepository.findById(request.getFollowUpAppointmentId())
          .orElse(null);
      surgery.setFollowUpAppointment(followUp);
    }

    // Create hospitalization record if needed
    if ("HOSPITALIZATION".equals(request.getDischargeType())) {
      createHospitalizationForSurgery(surgery);
    }

    Surgery updated = surgeryRepository.save(surgery);

    // Send post-op care SMS
    sendPostOpCareInstructions(surgery);

    log.info("Surgery completed ID: {}, discharge type: {}", surgeryId, request.getDischargeType());
    
    return surgeryMapper.toDto(updated);
  }

  @Override
  public void schedulePreOpSmsReminder(Long surgeryId) {
    log.info("Scheduling pre-op SMS reminder for surgery ID: {}", surgeryId);
    
    Surgery surgery = surgeryRepository.findById(surgeryId)
        .orElseThrow(() -> new ResourceNotFoundException("Surgery not found with id: " + surgeryId));

    // This method can be called manually or by the scheduler
    // The actual sending is handled by ReminderScheduler
    if (!Boolean.TRUE.equals(surgery.getPreOpSmsSent()) && "PLANNED".equals(surgery.getStatus())) {
      String ownerPhone = surgery.getAnimal().getOwner().getPhone();
      String petName = surgery.getAnimal().getName();
      int fastingHours = surgery.getFastingHours() != null ? surgery.getFastingHours() : 12;

      if (ownerPhone != null && !ownerPhone.isBlank()) {
        smsService.sendPreOperativeInstructions(ownerPhone, petName, surgery.getDate(), fastingHours);
        surgery.setPreOpSmsSent(true);
        surgery.setPreOpSmsSentAt(LocalDateTime.now());
        surgeryRepository.save(surgery);
        log.info("Pre-op SMS sent for surgery ID: {}", surgeryId);
      }
    }
  }

  // Private helper methods

  private void createHospitalizationForSurgery(Surgery surgery) {
    log.info("Creating hospitalization record for surgery ID: {}", surgery.getSurgeryId());
    
    Hospitalization hospitalization = Hospitalization.builder()
        .clinic(surgery.getClinic())
        .animal(surgery.getAnimal())
        .admissionDate(LocalDateTime.now())
        .status("ACTIVE")
        .primaryVeterinarian(surgery.getVeterinarianId() != null ? 
            "Vet ID: " + surgery.getVeterinarianId() : "Unknown")
        .diagnosisSummary("Post-operative care after surgery")
        .carePlan(surgery.getPostOpInstructions())
        .build();

    hospitalizationRepository.save(hospitalization);
    log.info("Hospitalization created for surgery ID: {}", surgery.getSurgeryId());
  }

  private void sendPostOpCareInstructions(Surgery surgery) {
    String ownerPhone = surgery.getAnimal().getOwner().getPhone();
    String petName = surgery.getAnimal().getName();
    String instructions = surgery.getPostOpInstructions();

    if (ownerPhone != null && !ownerPhone.isBlank() && instructions != null) {
      // Truncate if too long for SMS
      if (instructions.length() > 150) {
        instructions = instructions.substring(0, 147) + "...";
      }
      smsService.sendPostOperativeInstructions(ownerPhone, petName, instructions);
      log.info("Post-op care SMS sent for surgery ID: {}", surgery.getSurgeryId());
    }
  }
}
