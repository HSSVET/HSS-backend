package com.hss.hss_backend.scheduler;

import com.hss.hss_backend.entity.Appointment;
import com.hss.hss_backend.entity.Surgery;
import com.hss.hss_backend.entity.VaccinationRecord;
import com.hss.hss_backend.repository.AppointmentRepository;
import com.hss.hss_backend.repository.SurgeryRepository;
import com.hss.hss_backend.repository.VaccinationRecordRepository;
import com.hss.hss_backend.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Scheduler component for sending automated reminders via SMS.
 * Handles appointment reminders, pre-operative instructions, and vaccination reminders.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final AppointmentRepository appointmentRepository;
    private final SurgeryRepository surgeryRepository;
    private final VaccinationRecordRepository vaccinationRecordRepository;
    private final SmsService smsService;

    /**
     * Send appointment reminders for tomorrow's appointments.
     * Runs daily at 09:00.
     */
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional(readOnly = true)
    public void sendAppointmentReminders() {
        log.info("Starting appointment reminder job...");

        LocalDateTime tomorrowStart = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN);
        LocalDateTime tomorrowEnd = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MAX);

        List<Appointment> tomorrowAppointments = appointmentRepository.findByDateTimeBetween(
                tomorrowStart, tomorrowEnd);

        int sentCount = 0;
        for (Appointment appointment : tomorrowAppointments) {
            if (appointment.getStatus() == Appointment.Status.SCHEDULED ||
                    appointment.getStatus() == Appointment.Status.CONFIRMED) {
                try {
                    String ownerPhone = appointment.getAnimal().getOwner().getPhone();
                    String petName = appointment.getAnimal().getName();

                    if (ownerPhone != null && !ownerPhone.isBlank()) {
                        smsService.sendAppointmentReminder(ownerPhone, petName, appointment.getDateTime());
                        sentCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to send appointment reminder for appointment ID: {}",
                            appointment.getAppointmentId(), e);
                }
            }
        }

        log.info("Appointment reminder job completed. Sent {} reminders.", sentCount);
    }

    /**
     * Send pre-operative SMS instructions for surgeries scheduled for tomorrow.
     * Includes fasting instructions (8-12 hours before surgery).
     * Runs daily at 10:00.
     */
    @Scheduled(cron = "0 0 10 * * ?")
    @Transactional
    public void sendPreOperativeSMS() {
        log.info("Starting pre-operative SMS reminder job...");

        LocalDateTime tomorrowStart = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN);
        LocalDateTime tomorrowEnd = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MAX);

        List<Surgery> tomorrowSurgeries = surgeryRepository.findByDateBetween(tomorrowStart, tomorrowEnd);

        int sentCount = 0;
        for (Surgery surgery : tomorrowSurgeries) {
            // Only send if not already sent and surgery is planned
            if (!Boolean.TRUE.equals(surgery.getPreOpSmsSent()) && "PLANNED".equals(surgery.getStatus())) {
                try {
                    String ownerPhone = surgery.getAnimal().getOwner().getPhone();
                    String petName = surgery.getAnimal().getName();
                    int fastingHours = surgery.getFastingHours() != null ? surgery.getFastingHours() : 12;

                    if (ownerPhone != null && !ownerPhone.isBlank()) {
                        smsService.sendPreOperativeInstructions(ownerPhone, petName, surgery.getDate(), fastingHours);

                        // Mark SMS as sent
                        surgery.setPreOpSmsSent(true);
                        surgery.setPreOpSmsSentAt(LocalDateTime.now());
                        surgeryRepository.save(surgery);
                        sentCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to send pre-operative SMS for surgery ID: {}",
                            surgery.getSurgeryId(), e);
                }
            }
        }

        log.info("Pre-operative SMS job completed. Sent {} reminders.", sentCount);
    }

    /**
     * Send vaccination reminders for vaccinations due in the next 7 days.
     * Runs daily at 08:00.
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(readOnly = true)
    public void sendVaccinationReminders() {
        log.info("Starting vaccination reminder job...");

        LocalDate today = LocalDate.now();
        LocalDate oneWeekFromNow = today.plusDays(7);

        List<VaccinationRecord> dueVaccinations = vaccinationRecordRepository.findVaccinationsDueBetween(
                today, oneWeekFromNow);

        int sentCount = 0;
        for (VaccinationRecord record : dueVaccinations) {
            try {
                String ownerPhone = record.getAnimal().getOwner().getPhone();
                String petName = record.getAnimal().getName();
                String vaccineName = record.getVaccineName();
                LocalDate dueDate = record.getNextDueDate();

                if (ownerPhone != null && !ownerPhone.isBlank() && dueDate != null) {
                    smsService.sendVaccinationReminder(ownerPhone, petName, vaccineName,
                            dueDate.atStartOfDay());
                    sentCount++;
                }
            } catch (Exception e) {
                log.error("Failed to send vaccination reminder for record ID: {}",
                        record.getVaccinationRecordId(), e);
            }
        }

        log.info("Vaccination reminder job completed. Sent {} reminders.", sentCount);
    }

    /**
     * Send reminders for appointments happening in 2 hours.
     * Runs every hour at minute 0.
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional(readOnly = true)
    public void sendSameDayReminders() {
        log.info("Starting same-day appointment reminder job...");

        LocalDateTime twoHoursFromNow = LocalDateTime.now().plusHours(2);
        LocalDateTime twoHoursFromNowEnd = twoHoursFromNow.plusMinutes(59);

        List<Appointment> upcomingAppointments = appointmentRepository.findByDateTimeBetween(
                twoHoursFromNow, twoHoursFromNowEnd);

        int sentCount = 0;
        for (Appointment appointment : upcomingAppointments) {
            if (appointment.getStatus() == Appointment.Status.SCHEDULED ||
                    appointment.getStatus() == Appointment.Status.CONFIRMED) {
                try {
                    String ownerPhone = appointment.getAnimal().getOwner().getPhone();
                    String petName = appointment.getAnimal().getName();

                    if (ownerPhone != null && !ownerPhone.isBlank()) {
                        String message = String.format(
                                "Hatırlatma: %s için randevunuz 2 saat sonra! HSS Veteriner Kliniği",
                                petName);
                        smsService.sendSms(ownerPhone, message);
                        sentCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to send same-day reminder for appointment ID: {}",
                            appointment.getAppointmentId(), e);
                }
            }
        }

        log.info("Same-day reminder job completed. Sent {} reminders.", sentCount);
    }
}
