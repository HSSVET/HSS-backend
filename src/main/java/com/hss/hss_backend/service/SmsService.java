package com.hss.hss_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SMS Service for sending text messages
 * Note: This is a basic implementation that logs messages.
 * For production, integrate with an actual SMS provider (Twilio, AWS SNS, etc.)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

  @Value("${sms.enabled:false}")
  private boolean smsEnabled;

  @Value("${sms.provider:LOG_ONLY}")
  private String smsProvider;

  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  /**
   * Send appointment reminder SMS
   */
  public void sendAppointmentReminder(String phoneNumber, String petName, LocalDateTime appointmentTime) {
    String message = String.format(
        "Randevu Hatırlatma: %s için randevunuz %s tarihinde saat %s'de. HSS Veteriner Kliniği",
        petName,
        appointmentTime.format(DATE_FORMATTER),
        appointmentTime.format(TIME_FORMATTER));

    sendSms(phoneNumber, message);
  }

  /**
   * Send vaccination reminder SMS
   */
  public void sendVaccinationReminder(String phoneNumber, String petName, String vaccineName, LocalDateTime dueDate) {
    String message = String.format(
        "Aşı Hatırlatma: %s için %s aşısı %s tarihinde yapılmalıdır. Randevu için arayınız. HSS Veteriner Kliniği",
        petName,
        vaccineName,
        dueDate.format(DATE_FORMATTER));

    sendSms(phoneNumber, message);
  }

  /**
   * Send pre-operative instructions SMS (1 day before surgery)
   */
  public void sendPreOperativeInstructions(String phoneNumber, String petName, LocalDateTime surgeryTime,
      int fastingHours) {
    String message = String.format(
        "Operasyon Hatırlatma: %s için yarın saat %s'de operasyon. Lütfen %d saat aç bırakın (su hariç). HSS Veteriner Kliniği",
        petName,
        surgeryTime.format(TIME_FORMATTER),
        fastingHours);

    sendSms(phoneNumber, message);
  }

  /**
   * Send post-operative care instructions SMS
   */
  public void sendPostOperativeInstructions(String phoneNumber, String petName, String instructions) {
    String message = String.format(
        "Operasyon Sonrası Bakım - %s: %s HSS Veteriner Kliniği",
        petName,
        instructions);

    sendSms(phoneNumber, message);
  }

  /**
   * Send general SMS message
   */
  public void sendSms(String phoneNumber, String message) {
    if (!smsEnabled) {
      log.info("SMS sending disabled. Would send to {}: {}", phoneNumber, message);
      return;
    }

    try {
      switch (smsProvider.toUpperCase()) {
        case "TWILIO":
          sendViaTwilio(phoneNumber, message);
          break;
        case "AWS_SNS":
          sendViaAwsSns(phoneNumber, message);
          break;
        case "LOG_ONLY":
        default:
          log.info("SMS to {}: {}", phoneNumber, message);
          break;
      }
      log.info("SMS sent successfully to {}", phoneNumber);
    } catch (Exception e) {
      log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
      // Optionally, save failed SMS to database for retry
    }
  }

  /**
   * Schedule SMS to be sent at a specific time
   * For now, this just logs. In production, use a job scheduler like Quartz
   */
  public void scheduleSms(String phoneNumber, String message, LocalDateTime sendTime) {
    log.info("Scheduling SMS to {} at {}: {}", phoneNumber, sendTime, message);
    // TODO: Integrate with job scheduler (Quartz, Spring @Scheduled, etc.)
    // For now, just log the scheduled message
  }

  // Provider-specific implementations (placeholders for future integration)

  private void sendViaTwilio(String phoneNumber, String message) {
    // TODO: Implement Twilio integration
    // Example:
    // Message twilioMessage = Message.creator(
    // new PhoneNumber(phoneNumber),
    // new PhoneNumber(twilioFromNumber),
    // message
    // ).create();
    log.info("Twilio SMS to {}: {}", phoneNumber, message);
  }

  private void sendViaAwsSns(String phoneNumber, String message) {
    // TODO: Implement AWS SNS integration
    // Example:
    // PublishRequest request = new PublishRequest()
    // .withMessage(message)
    // .withPhoneNumber(phoneNumber);
    // snsClient.publish(request);
    log.info("AWS SNS SMS to {}: {}", phoneNumber, message);
  }
}
