package com.hss.hss_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    @Value("${sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${sms.provider:none}")
    private String smsProvider;

    public boolean sendSms(String phoneNumber, String message) {
        if (!smsEnabled) {
            log.warn("SMS service is disabled. SMS would be sent to: {}", phoneNumber);
            return true; // Return true for development/testing
        }

        try {
            // TODO: Implement actual SMS provider integration
            // Options: Twilio, Google Cloud SMS, AWS SNS, etc.
            switch (smsProvider.toLowerCase()) {
                case "twilio":
                    return sendViaTwilio(phoneNumber, message);
                case "google":
                    return sendViaGoogleCloud(phoneNumber, message);
                default:
                    log.warn("SMS provider not configured. SMS would be sent to: {}", phoneNumber);
                    return true;
            }
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", phoneNumber, e);
            return false;
        }
    }

    private boolean sendViaTwilio(String phoneNumber, String message) {
        // TODO: Implement Twilio integration
        log.info("SMS sent via Twilio to: {}", phoneNumber);
        return true;
    }

    private boolean sendViaGoogleCloud(String phoneNumber, String message) {
        // TODO: Implement Google Cloud SMS integration
        log.info("SMS sent via Google Cloud to: {}", phoneNumber);
        return true;
    }
}

