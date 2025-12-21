package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.AppointmentCreateRequest;
import com.hss.hss_backend.dto.request.AppointmentUpdateRequest;
import com.hss.hss_backend.dto.response.AppointmentResponse;
import com.hss.hss_backend.entity.Animal;
import com.hss.hss_backend.entity.Appointment;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.mapper.AppointmentMapper;
import com.hss.hss_backend.repository.AnimalRepository;
import com.hss.hss_backend.repository.AppointmentRepository;
import com.hss.hss_backend.security.ClinicContext;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AnimalRepository animalRepository;

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        log.info("Fetching appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        validateClinicAccess(appointment);

        return AppointmentMapper.toResponse(appointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getAllAppointments(Pageable pageable) {
        log.info("Fetching all appointments with pagination");
        Long clinicId = ClinicContext.getClinicId();

        Page<Appointment> appointments;
        if (clinicId != null) {
            appointments = appointmentRepository.findByClinicId(clinicId, pageable);
        } else {
            appointments = appointmentRepository.findAll(pageable);
        }

        return appointments.map(AppointmentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByAnimalId(Long animalId) {
        log.info("Fetching appointments for animal ID: {}", animalId);
        List<Appointment> appointments = appointmentRepository.findByAnimalAnimalId(animalId);
        return AppointmentMapper.toResponseList(appointments);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByVeterinarianId(Long veterinarianId) {
        log.info("Fetching appointments for veterinarian ID: {}", veterinarianId);
        List<Appointment> appointments = appointmentRepository.findByVeterinarianId(veterinarianId);
        return AppointmentMapper.toResponseList(appointments);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDateRange(LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        log.info("Fetching appointments between {} and {}", startDateTime, endDateTime);
        List<Appointment> appointments = appointmentRepository.findByDateTimeBetween(startDateTime, endDateTime);
        return AppointmentMapper.toResponseList(appointments);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByAnimalAndDateRange(Long animalId, LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        log.info("Fetching appointments for animal {} between {} and {}", animalId, startDateTime, endDateTime);
        List<Appointment> appointments = appointmentRepository.findByAnimalAnimalIdAndDateTimeBetween(animalId,
                startDateTime, endDateTime);
        return AppointmentMapper.toResponseList(appointments);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getUpcomingAppointments(LocalDateTime fromDateTime) {
        log.info("Fetching upcoming appointments from {}", fromDateTime);
        List<Appointment.Status> activeStatuses = List.of(
                Appointment.Status.SCHEDULED,
                Appointment.Status.CONFIRMED,
                Appointment.Status.IN_PROGRESS);
        List<Appointment> appointments = appointmentRepository.findUpcomingAppointments(fromDateTime, activeStatuses);
        return AppointmentMapper.toResponseList(appointments);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByStatus(Appointment.Status status) {
        log.info("Fetching appointments with status: {}", status);
        List<Appointment> appointments = appointmentRepository.findByStatus(status);
        return AppointmentMapper.toResponseList(appointments);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getTodayAppointments() {
        log.info("Fetching today's appointments");
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<Appointment> appointments = appointmentRepository.findByDateTimeBetween(startOfDay, endOfDay);
        return AppointmentMapper.toResponseList(appointments);
    }

    public AppointmentResponse createAppointment(AppointmentCreateRequest request) {
        log.info("Creating appointment for animal ID: {} at {}", request.getAnimalId(), request.getDateTime());

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal", request.getAnimalId()));

        Appointment appointment = Appointment.builder()
                .animal(animal)
                .dateTime(request.getDateTime())
                .subject(request.getSubject())
                .veterinarianId(request.getVeterinarianId())
                .status(Appointment.Status.SCHEDULED)
                .notes(request.getNotes())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created successfully with ID: {}", savedAppointment.getAppointmentId());
        return AppointmentMapper.toResponse(savedAppointment);
    }

    public AppointmentResponse createAppointment(Long animalId, LocalDateTime dateTime, String subject,
            Long veterinarianId, String notes) {
        log.info("Creating appointment for animal ID: {} at {}", animalId, dateTime);

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", animalId));

        Appointment appointment = Appointment.builder()
                .animal(animal)
                .dateTime(dateTime)
                .subject(subject)
                .veterinarianId(veterinarianId)
                .status(Appointment.Status.SCHEDULED)
                .notes(notes)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created successfully with ID: {}", savedAppointment.getAppointmentId());
        return AppointmentMapper.toResponse(savedAppointment);
    }

    private void validateClinicAccess(Appointment appointment) {
        Long currentClinicId = ClinicContext.getClinicId();
        if (currentClinicId != null && appointment.getAnimal() != null && appointment.getAnimal().getOwner() != null
                && appointment.getAnimal().getOwner().getClinic() != null) {
            Long recordClinicId = appointment.getAnimal().getOwner().getClinic().getClinicId();
            if (!currentClinicId.equals(recordClinicId)) {
                log.error("Access denied. Clinic ID {} cannot access appointment {} belonging to clinic {}",
                        currentClinicId, appointment.getAppointmentId(), recordClinicId);
                throw new AccessDeniedException("You do not have permission to access this appointment.");
            }
        }
    }

    public AppointmentResponse updateAppointmentStatus(Long id, Appointment.Status status) {
        log.info("Updating appointment {} status to {}", id, status);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        validateClinicAccess(appointment);

        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        log.info("Appointment status updated successfully");
        return AppointmentMapper.toResponse(updatedAppointment);
    }

    public AppointmentResponse updateAppointment(Long id, AppointmentUpdateRequest request) {
        log.info("Updating appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        validateClinicAccess(appointment);

        appointment.setDateTime(request.getDateTime());
        appointment.setSubject(request.getSubject());
        appointment.setVeterinarianId(request.getVeterinarianId());
        appointment.setNotes(request.getNotes());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment updated successfully with ID: {}", updatedAppointment.getAppointmentId());
        return AppointmentMapper.toResponse(updatedAppointment);
    }

    public AppointmentResponse updateAppointment(Long id, LocalDateTime dateTime, String subject, Long veterinarianId,
            String notes) {
        log.info("Updating appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        validateClinicAccess(appointment);

        appointment.setDateTime(dateTime);
        appointment.setSubject(subject);
        appointment.setVeterinarianId(veterinarianId);
        appointment.setNotes(notes);

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment updated successfully with ID: {}", updatedAppointment.getAppointmentId());
        return AppointmentMapper.toResponse(updatedAppointment);
    }

    public void deleteAppointment(Long id) {
        log.info("Deleting appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        validateClinicAccess(appointment);

        appointmentRepository.delete(appointment);
        log.info("Appointment deleted successfully with ID: {}", id);
    }
}
