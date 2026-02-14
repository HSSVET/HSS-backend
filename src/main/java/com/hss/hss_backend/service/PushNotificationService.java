package com.hss.hss_backend.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationService {

    private final FirebaseMessaging firebaseMessaging;

    @Value("${push.notification.enabled:true}")
    private boolean pushEnabled;

    public boolean sendPushNotification(String userToken, String title, String body) {
        if (!pushEnabled) {
            log.warn("Push notification service is disabled. Push would be sent to: {}", userToken);
            return true; // Return true for development/testing
        }

        try {
            Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

            Message message = Message.builder()
                .setToken(userToken)
                .setNotification(notification)
                .build();

            String response = firebaseMessaging.send(message);
            log.info("Push notification sent successfully. Response: {}", response);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification to: {}", userToken, e);
            return false;
        }
    }

    public boolean sendPushNotificationToTopic(String topic, String title, String body) {
        if (!pushEnabled) {
            log.warn("Push notification service is disabled. Push would be sent to topic: {}", topic);
            return true;
        }

        try {
            Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

            Message message = Message.builder()
                .setTopic(topic)
                .setNotification(notification)
                .build();

            String response = firebaseMessaging.send(message);
            log.info("Push notification sent to topic: {}. Response: {}", topic, response);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification to topic: {}", topic, e);
            return false;
        }
    }
}

