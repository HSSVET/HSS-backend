package com.hss.hss_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@hss.com}")
    private String fromEmail;

    @Value("${spring.mail.enabled:true}")
    private boolean emailEnabled;

    public boolean sendEmail(String to, String subject, String body) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Email would be sent to: {}", to);
            return true; // Return true for development/testing
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            return false;
        }
    }

    public boolean sendEmailWithHtml(String to, String subject, String htmlBody) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. HTML email would be sent to: {}", to);
            return true;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            
            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully to: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", to, e);
            return false;
        }
    }
}

