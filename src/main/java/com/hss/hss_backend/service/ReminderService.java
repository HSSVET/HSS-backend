package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.ReminderCreateRequest;
import com.hss.hss_backend.dto.response.ReminderResponse;
import com.hss.hss_backend.entity.Appointment;
import com.hss.hss_backend.entity.Reminder;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.repository.AppointmentRepository;
import com.hss.hss_backend.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    @Value("${reminder.default.hours.before:24}")
    private int defaultHoursBefore;

    @Value("${reminder.retry.max:3}")
    private int maxRetries;

    public ReminderResponse createReminder(ReminderCreateRequest request) {
        log.info("Creating reminder for appointment ID: {}", request.getAppointmentId());

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", request.getAppointmentId()));

        String message = request.getMessage();
        if (message == null || message.isBlank()) {
            message = generateDefaultMessage(appointment);
        }

        String recipientEmail = request.getRecipientEmail();
        String recipientPhone = request.getRecipientPhone();

        if (recipientEmail == null && appointment.getAnimal().getOwner().getEmail() != null) {
            recipientEmail = appointment.getAnimal().getOwner().getEmail();
        }

        if (recipientPhone == null && appointment.getAnimal().getOwner().getPhone() != null) {
            recipientPhone = appointment.getAnimal().getOwner().getPhone();
        }

        Reminder reminder = Reminder.builder()
                .appointment(appointment)
                .sendTime(request.getSendTime())
                .channel(request.getChannel())
                .status(Reminder.Status.PENDING)
                .message(message)
                .recipientEmail(recipientEmail)
                .recipientPhone(recipientPhone)
                .retryCount(0)
                .build();

        Reminder savedReminder = reminderRepository.save(reminder);
        log.info("Reminder created successfully with ID: {}", savedReminder.getReminderId());
        return toResponse(savedReminder);
    }

    public ReminderResponse createReminderForAppointment(Appointment appointment, Reminder.Channel channel) {
        log.info("Creating automatic reminder for appointment ID: {}", appointment.getAppointmentId());

        LocalDateTime sendTime = appointment.getDateTime().minusHours(defaultHoursBefore);
        String message = generateDefaultMessage(appointment);

        String recipientEmail = appointment.getAnimal().getOwner().getEmail();
        String recipientPhone = appointment.getAnimal().getOwner().getPhone();

        Reminder reminder = Reminder.builder()
                .appointment(appointment)
                .sendTime(sendTime)
                .channel(channel)
                .status(Reminder.Status.PENDING)
                .message(message)
                .recipientEmail(recipientEmail)
                .recipientPhone(recipientPhone)
                .retryCount(0)
                .build();

        Reminder savedReminder = reminderRepository.save(reminder);
        log.info("Automatic reminder created successfully with ID: {}", savedReminder.getReminderId());
        return toResponse(savedReminder);
    }

    public List<ReminderResponse> createDefaultRemindersForAppointment(Appointment appointment) {
        log.info("Creating default reminders for appointment ID: {}", appointment.getAppointmentId());

        List<ReminderResponse> reminders = new ArrayList<>();

        // 24 saat önce email hatırlatması
        if (appointment.getAnimal().getOwner().getEmail() != null) {
            ReminderResponse emailReminder = createReminderForAppointment(appointment, Reminder.Channel.EMAIL);
            reminders.add(emailReminder);
        }

        // 2 saat önce SMS hatırlatması
        if (appointment.getAnimal().getOwner().getPhone() != null) {
            LocalDateTime smsSendTime = appointment.getDateTime().minusHours(2);
            ReminderCreateRequest smsRequest = new ReminderCreateRequest();
            smsRequest.setAppointmentId(appointment.getAppointmentId());
            smsRequest.setSendTime(smsSendTime);
            smsRequest.setChannel(Reminder.Channel.SMS);
            smsRequest.setMessage(generateDefaultMessage(appointment));
            smsRequest.setRecipientPhone(appointment.getAnimal().getOwner().getPhone());

            ReminderResponse smsReminder = createReminder(smsRequest);
            reminders.add(smsReminder);
        }

        return reminders;
    }

    private String generateDefaultMessage(Appointment appointment) {
        return String.format(
            "Sayın %s %s,\n\n" +
            "%s adlı hayvanınız için %s tarihinde randevunuz bulunmaktadır.\n\n" +
            "Randevu Detayları:\n" +
            "Hayvan: %s\n" +
            "Tarih: %s\n" +
            "Konu: %s\n\n" +
            "Lütfen randevu saatinde hazır bulununuz.\n\n" +
            "Hayvan Sağlık Sistemi",
            appointment.getAnimal().getOwner().getFirstName(),
            appointment.getAnimal().getOwner().getLastName(),
            appointment.getAnimal().getName(),
            appointment.getDateTime().toString(),
            appointment.getAnimal().getName(),
            appointment.getDateTime().toString(),
            appointment.getSubject() != null ? appointment.getSubject() : "Genel Muayene"
        );
    }

    @Transactional(readOnly = true)
    public List<ReminderResponse> getRemindersByAppointmentId(Long appointmentId) {
        log.info("Fetching reminders for appointment ID: {}", appointmentId);
        List<Reminder> reminders = reminderRepository.findByAppointmentAppointmentId(appointmentId);
        return reminders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReminderResponse> getPendingReminders() {
        log.info("Fetching pending reminders");
        List<Reminder> reminders = reminderRepository.findByStatus(Reminder.Status.PENDING);
        return reminders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void sendReminder(Long reminderId) {
        log.info("Sending reminder ID: {}", reminderId);
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", reminderId));

        if (reminder.getStatus() != Reminder.Status.PENDING) {
            log.warn("Reminder {} is not in PENDING status. Current status: {}", reminderId, reminder.getStatus());
            return;
        }

        boolean success = notificationService.sendNotification(reminder);

        if (success) {
            reminder.setStatus(Reminder.Status.SENT);
            reminder.setSentAt(LocalDateTime.now());
            reminderRepository.save(reminder);
            log.info("Reminder {} sent successfully", reminderId);
        } else {
            reminder.setStatus(Reminder.Status.FAILED);
            reminder.setErrorMessage("Failed to send notification");
            reminder.setRetryCount(reminder.getRetryCount() + 1);
            reminderRepository.save(reminder);
            log.error("Failed to send reminder {}", reminderId);
        }
    }

    public void processPendingReminders() {
        log.info("Processing pending reminders");
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> pendingReminders = reminderRepository.findPendingRemindersToSend(now);

        log.info("Found {} pending reminders to send", pendingReminders.size());

        for (Reminder reminder : pendingReminders) {
            try {
                sendReminder(reminder.getReminderId());
            } catch (Exception e) {
                log.error("Error processing reminder {}", reminder.getReminderId(), e);
                reminder.setStatus(Reminder.Status.FAILED);
                reminder.setErrorMessage(e.getMessage());
                reminder.setRetryCount(reminder.getRetryCount() + 1);
                reminderRepository.save(reminder);
            }
        }
    }

    public void retryFailedReminders() {
        log.info("Retrying failed reminders");
        List<Reminder> failedReminders = reminderRepository.findFailedRemindersForRetry(maxRetries);

        log.info("Found {} failed reminders to retry", failedReminders.size());

        for (Reminder reminder : failedReminders) {
            reminder.setStatus(Reminder.Status.PENDING);
            reminder.setErrorMessage(null);
            reminderRepository.save(reminder);
            sendReminder(reminder.getReminderId());
        }
    }

    public void cancelReminder(Long reminderId) {
        log.info("Cancelling reminder ID: {}", reminderId);
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", reminderId));

        if (reminder.getStatus() == Reminder.Status.PENDING) {
            reminder.setStatus(Reminder.Status.CANCELLED);
            reminderRepository.save(reminder);
            log.info("Reminder {} cancelled successfully", reminderId);
        } else {
            log.warn("Cannot cancel reminder {} with status {}", reminderId, reminder.getStatus());
        }
    }

    private ReminderResponse toResponse(Reminder reminder) {
        return ReminderResponse.builder()
                .reminderId(reminder.getReminderId())
                .appointmentId(reminder.getAppointment().getAppointmentId())
                .sendTime(reminder.getSendTime())
                .channel(reminder.getChannel())
                .status(reminder.getStatus())
                .message(reminder.getMessage())
                .recipientEmail(reminder.getRecipientEmail())
                .recipientPhone(reminder.getRecipientPhone())
                .sentAt(reminder.getSentAt())
                .errorMessage(reminder.getErrorMessage())
                .retryCount(reminder.getRetryCount())
                .createdAt(reminder.getCreatedAt())
                .updatedAt(reminder.getUpdatedAt())
                .build();
    }
}

