package com.cinema.shared.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Push Notification Service for sending mobile push notifications
 * Supports Firebase Cloud Messaging (FCM)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationService {

    @Value("${app.notification.push.enabled:false}")
    private boolean pushEnabled;

    @Value("${app.notification.push.firebase-config:}")
    private String firebaseConfig;

    /**
     * Send push notification to a single device
     */
    @Async
    public void sendToDevice(String deviceToken, String title, String body, Map<String, String> data) {
        if (!pushEnabled) {
            log.info("Push disabled. Would send to token: {} title: {}", deviceToken, title);
            return;
        }

        if (firebaseConfig.isEmpty()) {
            log.warn("Firebase not configured. Cannot send push notification.");
            return;
        }

        try {
            // TODO: Implement Firebase Cloud Messaging
            // Example:
            // Message message = Message.builder()
            //     .setToken(deviceToken)
            //     .setNotification(Notification.builder()
            //         .setTitle(title)
            //         .setBody(body)
            //         .build())
            //     .putAllData(data)
            //     .build();
            // FirebaseMessaging.getInstance().send(message);

            log.info("Push notification sent to device: {} title: {}", deviceToken, title);
        } catch (Exception e) {
            log.error("Failed to send push notification to {}: {}", deviceToken, e.getMessage());
        }
    }

    /**
     * Send push notification to multiple devices
     */
    @Async
    public void sendToDevices(List<String> deviceTokens, String title, String body, Map<String, String> data) {
        if (!pushEnabled || deviceTokens.isEmpty()) {
            log.info("Push disabled or no tokens. Would send title: {}", title);
            return;
        }

        for (String token : deviceTokens) {
            sendToDevice(token, title, body, data);
        }
    }

    /**
     * Send push notification to a topic (all subscribers)
     */
    @Async
    public void sendToTopic(String topic, String title, String body, Map<String, String> data) {
        if (!pushEnabled) {
            log.info("Push disabled. Would send to topic: {} title: {}", topic, title);
            return;
        }

        try {
            // TODO: Implement Firebase topic messaging
            // Message message = Message.builder()
            //     .setTopic(topic)
            //     .setNotification(Notification.builder()
            //         .setTitle(title)
            //         .setBody(body)
            //         .build())
            //     .putAllData(data)
            //     .build();
            // FirebaseMessaging.getInstance().send(message);

            log.info("Push notification sent to topic: {} title: {}", topic, title);
        } catch (Exception e) {
            log.error("Failed to send push notification to topic {}: {}", topic, e.getMessage());
        }
    }

    // ==================== Booking Notifications ====================

    /**
     * Send booking confirmation push
     */
    public void sendBookingConfirmation(String deviceToken, String bookingCode,
                                         String movieTitle, String showDate, String showTime) {
        Map<String, String> data = Map.of(
            "type", "BOOKING_CONFIRMED",
            "bookingCode", bookingCode,
            "movieTitle", movieTitle
        );

        sendToDevice(deviceToken,
            "Dat ve thanh cong!",
            String.format("%s - %s %s", movieTitle, showDate, showTime),
            data);
    }

    /**
     * Send booking created (pending payment) push
     */
    public void sendBookingCreated(String deviceToken, String bookingCode, int expirationMinutes) {
        Map<String, String> data = Map.of(
            "type", "BOOKING_CREATED",
            "bookingCode", bookingCode,
            "expirationMinutes", String.valueOf(expirationMinutes)
        );

        sendToDevice(deviceToken,
            "Vui long thanh toan!",
            String.format("Dat ve #%s can thanh toan trong %d phut", bookingCode, expirationMinutes),
            data);
    }

    /**
     * Send booking cancelled push
     */
    public void sendBookingCancelled(String deviceToken, String bookingCode, String reason) {
        Map<String, String> data = Map.of(
            "type", "BOOKING_CANCELLED",
            "bookingCode", bookingCode
        );

        sendToDevice(deviceToken,
            "Dat ve da bi huy",
            String.format("Dat ve #%s: %s", bookingCode, reason),
            data);
    }

    /**
     * Send booking reminder push (before showtime)
     */
    public void sendShowReminder(String deviceToken, String bookingCode,
                                  String movieTitle, String cinemaName, int minutesBefore) {
        Map<String, String> data = Map.of(
            "type", "SHOW_REMINDER",
            "bookingCode", bookingCode,
            "movieTitle", movieTitle
        );

        sendToDevice(deviceToken,
            "Nhac nho lich chieu!",
            String.format("%s tai %s se bat dau trong %d phut", movieTitle, cinemaName, minutesBefore),
            data);
    }

    // ==================== Promotional Notifications ====================

    /**
     * Send new movie notification to all users
     */
    public void notifyNewMovie(String movieTitle, String releaseDate) {
        Map<String, String> data = Map.of(
            "type", "NEW_MOVIE",
            "movieTitle", movieTitle
        );

        sendToTopic("new_movies",
            "Phim moi sap chieu!",
            String.format("%s - Khoi chieu %s", movieTitle, releaseDate),
            data);
    }

    /**
     * Send promotion notification
     */
    public void notifyPromotion(String title, String description) {
        Map<String, String> data = Map.of(
            "type", "PROMOTION"
        );

        sendToTopic("promotions", title, description, data);
    }
}
