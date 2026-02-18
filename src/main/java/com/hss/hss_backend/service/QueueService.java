package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.QueueCheckInRequest;
import com.hss.hss_backend.dto.response.QueueEntryResponse;
import com.hss.hss_backend.entity.*;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.repository.*;
import com.hss.hss_backend.security.ClinicContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QueueService {

  private final QueueEntryRepository queueEntryRepository;
  private final AppointmentRepository appointmentRepository;
  private final AnimalRepository animalRepository;
  private final ClinicRepository clinicRepository;
  private final AppointmentService appointmentService;

  /**
   * Check in a patient with an existing appointment
   */
  public QueueEntryResponse checkInWithAppointment(Long appointmentId) {
    log.info("Checking in patient with appointment ID: {}", appointmentId);

    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

    // Check if already checked in today
    LocalDate today = LocalDate.now();
    queueEntryRepository.findByAnimalAnimalIdAndQueueDate(appointment.getAnimal().getAnimalId(), today)
        .ifPresent(existing -> {
          throw new IllegalStateException(
              "Animal already checked in today. Queue number: " + existing.getQueueNumber());
        });

    return createQueueEntry(appointment, QueueEntry.Priority.NORMAL, null);
  }

  /**
   * Check in a walk-in patient without appointment
   */
  public QueueEntryResponse walkInCheckIn(QueueCheckInRequest request) {
    log.info("Walk-in check-in for animal ID: {}", request.getAnimalId());

    // Fetch animal with owner and clinic eagerly to avoid lazy loading issues
    Animal animal = animalRepository.findByIdWithOwnerAndClinic(request.getAnimalId())
        .orElseThrow(() -> new ResourceNotFoundException("Animal", request.getAnimalId()));

    // Get clinic ID from the animal's owner (most reliable source)
    Clinic clinic = animal.getOwner() != null ? animal.getOwner().getClinic() : null;
    if (clinic == null) {
      throw new IllegalStateException("Animal's owner does not have an associated clinic. Please ensure the animal is registered to an owner with a clinic.");
    }
    
    Long clinicId = clinic.getClinicId();
    log.info("Using clinic ID {} from animal's owner for animal {}", clinicId, animal.getName());

    // Check if already checked in today
    LocalDate today = LocalDate.now();
    if (queueEntryRepository.findByAnimalAnimalIdAndQueueDate(request.getAnimalId(), today).isPresent()) {
      throw new IllegalStateException("Animal has already checked in today");
    }

    // Create a new appointment for this walk-in
    Appointment appointment = Appointment.builder()
        .clinic(clinic)
        .animal(animal)
        .dateTime(LocalDateTime.now())
        .subject("Walk-in: " + (request.getAppointmentType() != null ? request.getAppointmentType() : "General Exam"))
        .appointmentType(parseAppointmentType(request.getAppointmentType()))
        .status(Appointment.Status.IN_PROGRESS)
        .notes(request.getNotes())
        .build();

    appointment = appointmentRepository.save(appointment);

    QueueEntry.Priority priority = parsePriority(request.getPriority());
    return createQueueEntry(appointment, priority, request.getNotes());
  }

  /**
   * Create a queue entry for an appointment
   */
  private QueueEntryResponse createQueueEntry(Appointment appointment, QueueEntry.Priority priority, String notes) {
    // Get clinic ID from the appointment (most reliable source)
    Clinic clinic = appointment.getClinic();
    if (clinic == null) {
      throw new IllegalStateException("Appointment does not have an associated clinic");
    }
    
    Long clinicId = clinic.getClinicId();
    log.info("Using clinic ID {} from appointment", clinicId);

    LocalDate today = LocalDate.now();

    // Get next queue number
    Integer queueNumber = queueEntryRepository.findNextQueueNumber(clinicId, today);

    // Calculate estimated start time
    LocalDateTime estimatedStartTime = calculateEstimatedStartTime(clinicId, today);

    // Create queue entry
    QueueEntry queueEntry = QueueEntry.builder()
        .clinic(appointment.getClinic())
        .appointment(appointment)
        .animal(appointment.getAnimal())
        .queueNumber(queueNumber)
        .queueDate(today)
        .status(QueueEntry.QueueStatus.WAITING)
        .priority(priority)
        .checkInTime(LocalDateTime.now())
        .estimatedStartTime(estimatedStartTime)
        .estimatedDurationMinutes(30) // Default 30 minutes
        .notes(notes)
        .build();

    queueEntry = queueEntryRepository.save(queueEntry);

    // Update appointment with check-in info
    appointment.setCheckInTime(LocalDateTime.now());
    appointment.setQueueNumber(queueNumber);
    appointment.setEstimatedStartTime(estimatedStartTime);
    appointment.setStatus(appointment.getStatus() == Appointment.Status.SCHEDULED ? Appointment.Status.CONFIRMED
        : appointment.getStatus());
    appointmentRepository.save(appointment);

    log.info("Created queue entry with number {} for appointment {}", queueNumber, appointment.getAppointmentId());

    return mapToResponse(queueEntry);
  }

  /**
   * Get today's queue for the current clinic
   */
  public List<QueueEntryResponse> getTodayQueue() {
    // Get clinic ID from context, fallback to first available clinic
    Long clinicId = ClinicContext.getClinicId();
    
    if (clinicId == null) {
      // Fallback: use the first available clinic
      clinicId = clinicRepository.findAll().stream()
          .findFirst()
          .map(Clinic::getClinicId)
          .orElseThrow(() -> new IllegalStateException("No clinic found in the system"));
      log.warn("Clinic context not set, using first available clinic: {}", clinicId);
    }

    LocalDate today = LocalDate.now();

    List<QueueEntry> queueEntries = queueEntryRepository
        .findByClinicClinicIdAndQueueDateOrderByQueueNumberAsc(clinicId, today);

    return queueEntries.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  /**
   * Get active queue (waiting + in progress)
   */
  public List<QueueEntryResponse> getActiveQueue() {
    // Get clinic ID from context, fallback to first available clinic
    Long clinicId = ClinicContext.getClinicId();
    
    if (clinicId == null) {
      // Fallback: use the first available clinic
      clinicId = clinicRepository.findAll().stream()
          .findFirst()
          .map(Clinic::getClinicId)
          .orElseThrow(() -> new IllegalStateException("No clinic found in the system"));
      log.warn("Clinic context not set, using first available clinic: {}", clinicId);
    }

    LocalDate today = LocalDate.now();

    List<QueueEntry.QueueStatus> activeStatuses = Arrays.asList(
        QueueEntry.QueueStatus.WAITING,
        QueueEntry.QueueStatus.IN_PROGRESS);

    List<QueueEntry> queueEntries = queueEntryRepository
        .findByClinicClinicIdAndQueueDateAndStatusInOrderByPriorityDescQueueNumberAsc(
            clinicId, today, activeStatuses);

    return queueEntries.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  /**
   * Update queue status
   */
  public QueueEntryResponse updateQueueStatus(Long queueEntryId, QueueEntry.QueueStatus newStatus) {
    log.info("Updating queue entry {} to status: {}", queueEntryId, newStatus);

    QueueEntry queueEntry = queueEntryRepository.findById(queueEntryId)
        .orElseThrow(() -> new ResourceNotFoundException("QueueEntry", queueEntryId));

    QueueEntry.QueueStatus oldStatus = queueEntry.getStatus();
    queueEntry.setStatus(newStatus);

    // Update timestamps based on status
    switch (newStatus) {
      case WAITING:
        // Reset to waiting state, no special handling needed
        break;
      case IN_PROGRESS:
        queueEntry.setStartedTime(LocalDateTime.now());
        if (queueEntry.getAppointment() != null) {
          queueEntry.getAppointment().setStatus(Appointment.Status.IN_PROGRESS);
        }
        break;
      case COMPLETED:
        queueEntry.setCompletedTime(LocalDateTime.now());
        if (queueEntry.getAppointment() != null) {
          queueEntry.getAppointment().setStatus(Appointment.Status.COMPLETED);
        }
        break;
      case CANCELLED:
        if (queueEntry.getAppointment() != null) {
          queueEntry.getAppointment().setStatus(Appointment.Status.CANCELLED);
        }
        break;
      case NO_SHOW:
        if (queueEntry.getAppointment() != null) {
          queueEntry.getAppointment().setStatus(Appointment.Status.NO_SHOW);
        }
        break;
    }

    queueEntry = queueEntryRepository.save(queueEntry);

    log.info("Queue entry {} status changed from {} to {}", queueEntryId, oldStatus, newStatus);

    return mapToResponse(queueEntry);
  }

  /**
   * Assign veterinarian to queue entry
   */
  public QueueEntryResponse assignVeterinarian(Long queueEntryId, Long veterinarianId, String room) {
    QueueEntry queueEntry = queueEntryRepository.findById(queueEntryId)
        .orElseThrow(() -> new ResourceNotFoundException("QueueEntry", queueEntryId));

    queueEntry.setAssignedVeterinarianId(veterinarianId);
    queueEntry.setAssignedRoom(room);

    queueEntry = queueEntryRepository.save(queueEntry);

    log.info("Assigned veterinarian {} and room {} to queue entry {}",
        veterinarianId, room, queueEntryId);

    return mapToResponse(queueEntry);
  }

  /**
   * Get next patient for veterinarian
   */
  public QueueEntryResponse getNextPatient(Long veterinarianId) {
    // Get clinic ID from context, fallback to first available clinic
    Long clinicId = ClinicContext.getClinicId();
    
    if (clinicId == null) {
      // Fallback: use the first available clinic
      clinicId = clinicRepository.findAll().stream()
          .findFirst()
          .map(Clinic::getClinicId)
          .orElseThrow(() -> new IllegalStateException("No clinic found in the system"));
      log.warn("Clinic context not set, using first available clinic: {}", clinicId);
    }

    LocalDate today = LocalDate.now();

    // First, look for waiting patients assigned to this veterinarian
    List<QueueEntry> assignedWaiting = queueEntryRepository
        .findByAssignedVeterinarianIdAndQueueDateAndStatusOrderByQueueNumberAsc(
            veterinarianId, today, QueueEntry.QueueStatus.WAITING);

    if (!assignedWaiting.isEmpty()) {
      return mapToResponse(assignedWaiting.get(0));
    }

    // Otherwise, get the next waiting patient (not assigned)
    List<QueueEntry.QueueStatus> waitingStatus = Arrays.asList(QueueEntry.QueueStatus.WAITING);
    List<QueueEntry> allWaiting = queueEntryRepository
        .findByClinicClinicIdAndQueueDateAndStatusInOrderByPriorityDescQueueNumberAsc(
            clinicId, today, waitingStatus);

    if (!allWaiting.isEmpty()) {
      return mapToResponse(allWaiting.get(0));
    }

    return null; // No patients waiting
  }

  /**
   * Get estimated wait time for a queue entry
   */
  public Integer getEstimatedWaitMinutes(Long queueEntryId) {
    QueueEntry queueEntry = queueEntryRepository.findById(queueEntryId)
        .orElseThrow(() -> new ResourceNotFoundException("QueueEntry", queueEntryId));

    if (queueEntry.getStatus() == QueueEntry.QueueStatus.IN_PROGRESS ||
        queueEntry.getStatus() == QueueEntry.QueueStatus.COMPLETED) {
      return 0;
    }

    // Count patients ahead in queue
    Long clinicId = queueEntry.getClinic().getClinicId();
    LocalDate queueDate = queueEntry.getQueueDate();

    List<QueueEntry.QueueStatus> activeStatuses = Arrays.asList(
        QueueEntry.QueueStatus.WAITING,
        QueueEntry.QueueStatus.IN_PROGRESS);

    List<QueueEntry> activeQueue = queueEntryRepository
        .findByClinicClinicIdAndQueueDateAndStatusInOrderByPriorityDescQueueNumberAsc(
            clinicId, queueDate, activeStatuses);

    int patientsAhead = 0;
    for (QueueEntry entry : activeQueue) {
      if (entry.getQueueEntryId().equals(queueEntryId)) {
        break;
      }
      patientsAhead++;
    }

    // Estimate: average 30 minutes per patient
    return patientsAhead * 30;
  }

  /**
   * Calculate estimated start time based on current queue
   */
  private LocalDateTime calculateEstimatedStartTime(Long clinicId, LocalDate queueDate) {
    List<QueueEntry.QueueStatus> activeStatuses = Arrays.asList(
        QueueEntry.QueueStatus.WAITING,
        QueueEntry.QueueStatus.IN_PROGRESS);

    Long activeCount = queueEntryRepository.countByClinicAndDateAndStatus(clinicId, queueDate, activeStatuses);

    // Estimate: 30 minutes per patient currently in queue
    return LocalDateTime.now().plusMinutes(activeCount * 30);
  }

  /**
   * Map entity to response DTO
   */
  private QueueEntryResponse mapToResponse(QueueEntry queueEntry) {
    Integer estimatedWait = getEstimatedWaitMinutes(queueEntry.getQueueEntryId());

    return QueueEntryResponse.builder()
        .queueEntryId(queueEntry.getQueueEntryId())
        .clinicId(queueEntry.getClinic().getClinicId())
        .appointmentId(queueEntry.getAppointment() != null ? queueEntry.getAppointment().getAppointmentId() : null)
        .animalId(queueEntry.getAnimal().getAnimalId())
        .animalName(queueEntry.getAnimal().getName())
        .ownerName(queueEntry.getAnimal().getOwner() != null ? queueEntry.getAnimal().getOwner().getFirstName() + " " +
            queueEntry.getAnimal().getOwner().getLastName() : "Unknown")
        .queueNumber(queueEntry.getQueueNumber())
        .status(queueEntry.getStatus().name())
        .priority(queueEntry.getPriority().name())
        .checkInTime(queueEntry.getCheckInTime())
        .estimatedStartTime(queueEntry.getEstimatedStartTime())
        .estimatedWaitMinutes(estimatedWait)
        .assignedVeterinarianId(queueEntry.getAssignedVeterinarianId())
        .assignedRoom(queueEntry.getAssignedRoom())
        .notes(queueEntry.getNotes())
        .build();
  }

  // Helper methods
  private Appointment.AppointmentType parseAppointmentType(String type) {
    if (type == null)
      return Appointment.AppointmentType.GENERAL_EXAM;
    try {
      return Appointment.AppointmentType.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException e) {
      return Appointment.AppointmentType.GENERAL_EXAM;
    }
  }

  private QueueEntry.Priority parsePriority(String priority) {
    if (priority == null)
      return QueueEntry.Priority.NORMAL;
    try {
      return QueueEntry.Priority.valueOf(priority.toUpperCase());
    } catch (IllegalArgumentException e) {
      return QueueEntry.Priority.NORMAL;
    }
  }
}
