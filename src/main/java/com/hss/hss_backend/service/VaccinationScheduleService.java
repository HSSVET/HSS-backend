package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.response.VaccinationScheduleResponse;
import com.hss.hss_backend.entity.*;
import com.hss.hss_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VaccinationScheduleService {

    private final VaccinationScheduleRepository scheduleRepository;
    private final VaccinationProtocolRepository protocolRepository;
    private final VaccinationRecordRepository recordRepository;
    private final AnimalRepository animalRepository;

    public void generateScheduleForAnimal(Long animalId) {
        log.info("Generating vaccination schedule for animal ID: {}", animalId);
        
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal not found: " + animalId));

        Long speciesId = animal.getSpecies().getSpeciesId();
        List<VaccinationProtocol> protocols = protocolRepository.findActiveProtocolsBySpeciesId(speciesId);

        if (protocols.isEmpty()) {
            log.warn("No active vaccination protocols found for species ID: {}", speciesId);
            return;
        }

        LocalDate birthDate = animal.getBirthDate();
        if (birthDate == null) {
            log.warn("Animal {} has no birth date, cannot generate schedule", animalId);
            return;
        }

        List<VaccinationSchedule> newSchedules = new ArrayList<>();

        for (VaccinationProtocol protocol : protocols) {
            // Mevcut aşı kayıtlarını kontrol et
            List<VaccinationRecord> existingRecords = recordRepository.findByAnimalAnimalIdAndVaccineVaccineId(
                animalId, protocol.getVaccine().getVaccineId());

            // Son aşı tarihini bul
            LocalDate lastVaccinationDate = existingRecords.stream()
                .map(VaccinationRecord::getDate)
                .max(LocalDate::compareTo)
                .orElse(null);

            // Eğer son aşı varsa ve nextDueDate varsa, onu kullan
            if (lastVaccinationDate != null) {
                VaccinationRecord lastRecord = existingRecords.stream()
                    .filter(r -> r.getDate().equals(lastVaccinationDate))
                    .findFirst()
                    .orElse(null);

                if (lastRecord != null && lastRecord.getNextDueDate() != null) {
                    createScheduleFromNextDueDate(animal, protocol, lastRecord.getNextDueDate(), newSchedules);
                    continue;
                }
            }

            // Yeni aşı takvimi oluştur
            createInitialSchedule(animal, protocol, birthDate, newSchedules);
        }

        if (!newSchedules.isEmpty()) {
            scheduleRepository.saveAll(newSchedules);
            log.info("Created {} vaccination schedules for animal {}", newSchedules.size(), animalId);
        }
    }

    private void createInitialSchedule(Animal animal, VaccinationProtocol protocol, LocalDate birthDate, 
                                      List<VaccinationSchedule> schedules) {
        LocalDate firstDoseDate = birthDate.plusWeeks(protocol.getFirstDoseAgeWeeks());
        Vaccine vaccine = protocol.getVaccine();

        // İlk dozlar
        for (int dose = 1; dose <= protocol.getTotalDoses(); dose++) {
            LocalDate scheduledDate = firstDoseDate.plusWeeks((dose - 1) * protocol.getDoseIntervalWeeks());
            
            // Geçmiş tarihli aşıları oluşturma
            if (scheduledDate.isBefore(LocalDate.now())) {
                continue;
            }

            VaccinationSchedule schedule = VaccinationSchedule.builder()
                .animal(animal)
                .vaccine(vaccine)
                .protocol(protocol)
                .scheduledDate(scheduledDate)
                .doseNumber(dose)
                .status(VaccinationSchedule.Status.PENDING)
                .priority(calculatePriority(scheduledDate))
                .isOverdue(false)
                .build();

            schedules.add(schedule);
        }

        // Booster aşıları (eğer gerekliyse)
        if (protocol.getBoosterIntervalMonths() != null && protocol.getBoosterIntervalMonths() > 0) {
            LocalDate lastDoseDate = firstDoseDate.plusWeeks((protocol.getTotalDoses() - 1) * protocol.getDoseIntervalWeeks());
            LocalDate boosterDate = lastDoseDate.plusMonths(protocol.getBoosterIntervalMonths());
            
            if (!boosterDate.isBefore(LocalDate.now())) {
                VaccinationSchedule boosterSchedule = VaccinationSchedule.builder()
                    .animal(animal)
                    .vaccine(vaccine)
                    .protocol(protocol)
                    .scheduledDate(boosterDate)
                    .doseNumber(protocol.getTotalDoses() + 1)
                    .status(VaccinationSchedule.Status.PENDING)
                    .priority(calculatePriority(boosterDate))
                    .isOverdue(false)
                    .build();

                schedules.add(boosterSchedule);
            }
        }
    }

    private void createScheduleFromNextDueDate(Animal animal, VaccinationProtocol protocol, 
                                              LocalDate nextDueDate, List<VaccinationSchedule> schedules) {
        Vaccine vaccine = protocol.getVaccine();

        // Mevcut pending schedule var mı kontrol et
        List<VaccinationSchedule> existingSchedules = scheduleRepository
            .findPendingSchedulesByAnimalAndVaccine(animal.getAnimalId(), vaccine.getVaccineId());

        if (!existingSchedules.isEmpty()) {
            log.debug("Schedule already exists for animal {} and vaccine {}", animal.getAnimalId(), vaccine.getVaccineId());
            return;
        }

        VaccinationSchedule schedule = VaccinationSchedule.builder()
            .animal(animal)
            .vaccine(vaccine)
            .protocol(protocol)
            .scheduledDate(nextDueDate)
            .doseNumber(1)
            .status(VaccinationSchedule.Status.PENDING)
            .priority(calculatePriority(nextDueDate))
            .isOverdue(nextDueDate.isBefore(LocalDate.now()))
            .build();

        schedules.add(schedule);
    }

    private VaccinationSchedule.Priority calculatePriority(LocalDate scheduledDate) {
        LocalDate today = LocalDate.now();
        long daysUntil = ChronoUnit.DAYS.between(today, scheduledDate);

        if (scheduledDate.isBefore(today)) {
            return VaccinationSchedule.Priority.CRITICAL; // Gecikmiş
        } else if (daysUntil <= 7) {
            return VaccinationSchedule.Priority.HIGH; // 7 gün içinde
        } else if (daysUntil <= 30) {
            return VaccinationSchedule.Priority.MEDIUM; // 30 gün içinde
        } else {
            return VaccinationSchedule.Priority.LOW; // 30 günden fazla
        }
    }

    public void checkAndUpdateOverdueSchedules() {
        log.info("Checking for overdue vaccination schedules");
        LocalDate today = LocalDate.now();
        
        List<VaccinationSchedule> overdueSchedules = scheduleRepository.findOverdueSchedules(today);
        
        for (VaccinationSchedule schedule : overdueSchedules) {
            if (!schedule.getIsOverdue()) {
                schedule.setIsOverdue(true);
                schedule.setStatus(VaccinationSchedule.Status.OVERDUE);
                schedule.setPriority(VaccinationSchedule.Priority.CRITICAL);
                log.info("Marked schedule {} as overdue", schedule.getScheduleId());
            }
        }
        
        if (!overdueSchedules.isEmpty()) {
            scheduleRepository.saveAll(overdueSchedules);
            log.info("Updated {} overdue schedules", overdueSchedules.size());
        }
    }

    public void markScheduleAsCompleted(Long scheduleId, Long vaccinationRecordId) {
        log.info("Marking schedule {} as completed with record {}", scheduleId, vaccinationRecordId);
        
        VaccinationSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + scheduleId));

        schedule.setStatus(VaccinationSchedule.Status.COMPLETED);
        schedule.setCompletedDate(LocalDate.now());
        schedule.setIsOverdue(false);
        
        if (vaccinationRecordId != null) {
            VaccinationRecord record = recordRepository.findById(vaccinationRecordId)
                    .orElseThrow(() -> new RuntimeException("Vaccination record not found: " + vaccinationRecordId));
            schedule.setVaccinationRecord(record);
        }

        scheduleRepository.save(schedule);

        // Eğer booster gerekiyorsa, bir sonraki aşıyı planla
        if (schedule.getProtocol() != null && schedule.getProtocol().getBoosterIntervalMonths() != null) {
            scheduleNextBooster(schedule);
        }
    }

    private void scheduleNextBooster(VaccinationSchedule completedSchedule) {
        VaccinationProtocol protocol = completedSchedule.getProtocol();
        if (protocol.getBoosterIntervalMonths() == null || protocol.getBoosterIntervalMonths() <= 0) {
            return;
        }

        LocalDate nextBoosterDate = completedSchedule.getCompletedDate()
            .plusMonths(protocol.getBoosterIntervalMonths());

        // Mevcut booster schedule var mı kontrol et
        List<VaccinationSchedule> existingSchedules = scheduleRepository
            .findPendingSchedulesByAnimalAndVaccine(
                completedSchedule.getAnimal().getAnimalId(),
                completedSchedule.getVaccine().getVaccineId());

        boolean boosterExists = existingSchedules.stream()
            .anyMatch(s -> s.getScheduledDate().equals(nextBoosterDate));

        if (!boosterExists) {
            VaccinationSchedule boosterSchedule = VaccinationSchedule.builder()
                .animal(completedSchedule.getAnimal())
                .vaccine(completedSchedule.getVaccine())
                .protocol(protocol)
                .scheduledDate(nextBoosterDate)
                .doseNumber(completedSchedule.getDoseNumber() + 1)
                .status(VaccinationSchedule.Status.PENDING)
                .priority(calculatePriority(nextBoosterDate))
                .isOverdue(false)
                .build();

            scheduleRepository.save(boosterSchedule);
            log.info("Created next booster schedule for animal {}", completedSchedule.getAnimal().getAnimalId());
        }
    }

    @Transactional(readOnly = true)
    public List<VaccinationScheduleResponse> getSchedulesByAnimalId(Long animalId) {
        log.info("Fetching vaccination schedules for animal ID: {}", animalId);
        List<VaccinationSchedule> schedules = scheduleRepository.findByAnimalAnimalId(animalId);
        return schedules.stream()
            .sorted(Comparator.comparing(VaccinationSchedule::getScheduledDate))
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VaccinationScheduleResponse> getPendingSchedules() {
        log.info("Fetching pending vaccination schedules");
        List<VaccinationSchedule> schedules = scheduleRepository.findByStatus(VaccinationSchedule.Status.PENDING);
        return schedules.stream()
            .sorted(Comparator.comparing(VaccinationSchedule::getScheduledDate))
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VaccinationScheduleResponse> getOverdueSchedules() {
        log.info("Fetching overdue vaccination schedules");
        List<VaccinationSchedule> schedules = scheduleRepository.findOverduePendingSchedules();
        return schedules.stream()
            .sorted(Comparator.comparing(VaccinationSchedule::getScheduledDate))
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VaccinationScheduleResponse> getUpcomingSchedules(int days) {
        log.info("Fetching upcoming vaccination schedules for next {} days", days);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        List<VaccinationSchedule> schedules = scheduleRepository.findSchedulesBetween(today, endDate);
        return schedules.stream()
            .sorted(Comparator.comparing(VaccinationSchedule::getScheduledDate))
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private VaccinationScheduleResponse toResponse(VaccinationSchedule schedule) {
        return VaccinationScheduleResponse.builder()
            .scheduleId(schedule.getScheduleId())
            .animalId(schedule.getAnimal().getAnimalId())
            .animalName(schedule.getAnimal().getName())
            .vaccineId(schedule.getVaccine().getVaccineId())
            .vaccineName(schedule.getVaccine().getVaccineName())
            .protocolId(schedule.getProtocol() != null ? schedule.getProtocol().getProtocolId() : null)
            .protocolName(schedule.getProtocol() != null ? schedule.getProtocol().getProtocolName() : null)
            .scheduledDate(schedule.getScheduledDate())
            .doseNumber(schedule.getDoseNumber())
            .status(schedule.getStatus())
            .priority(schedule.getPriority())
            .isOverdue(schedule.getIsOverdue())
            .completedDate(schedule.getCompletedDate())
            .vaccinationRecordId(schedule.getVaccinationRecord() != null ? 
                schedule.getVaccinationRecord().getVaccinationRecordId() : null)
            .notes(schedule.getNotes())
            .createdAt(schedule.getCreatedAt() != null ? schedule.getCreatedAt().toLocalDate() : null)
            .updatedAt(schedule.getUpdatedAt() != null ? schedule.getUpdatedAt().toLocalDate() : null)
            .build();
    }
}

