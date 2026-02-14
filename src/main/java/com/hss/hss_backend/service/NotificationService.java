package com.hss.hss_backend.service;

import com.hss.hss_backend.entity.Reminder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushNotificationService;

    public boolean sendNotification(Reminder reminder) {
        try {
            boolean success = false;
            
            switch (reminder.getChannel()) {
                case EMAIL:
                    success = emailService.sendEmail(
                        reminder.getRecipientEmail(),
                        "Randevu Hatırlatması",
                        reminder.getMessage()
                    );
                    break;
                case SMS:
                    success = smsService.sendSms(
                        reminder.getRecipientPhone(),
                        reminder.getMessage()
                    );
                    break;
                case PUSH:
                    success = pushNotificationService.sendPushNotification(
                        reminder.getRecipientEmail(),
                        "Randevu Hatırlatması",
                        reminder.getMessage()
                    );
                    break;
            }
            
            if (success) {
                log.info("Notification sent successfully for reminder ID: {}", reminder.getReminderId());
            } else {
                log.warn("Failed to send notification for reminder ID: {}", reminder.getReminderId());
            }
            
            return success;
        } catch (Exception e) {
            log.error("Error sending notification for reminder ID: {}", reminder.getReminderId(), e);
            return false;
        }
    }
}

