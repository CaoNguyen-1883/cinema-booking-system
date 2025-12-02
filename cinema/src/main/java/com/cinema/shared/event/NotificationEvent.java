package com.cinema.shared.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Notification event for sending emails, SMS, push notifications
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String eventId;
    private LocalDateTime timestamp;
    private NotificationType type;
    private String templateName;
    private RecipientData recipient;
    private Map<String, Object> data;

    public enum NotificationType {
        EMAIL,
        SMS,
        PUSH
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipientData {
        private Long userId;
        private String email;
        private String phoneNumber;
        private String deviceToken;  // For push notifications
    }

    // Template names
    public static final String TEMPLATE_BOOKING_CREATED = "booking-created";
    public static final String TEMPLATE_BOOKING_CONFIRMED = "booking-confirmed";
    public static final String TEMPLATE_BOOKING_CANCELLED = "booking-cancelled";
    public static final String TEMPLATE_BOOKING_EXPIRED = "booking-expired";
    public static final String TEMPLATE_PAYMENT_SUCCESS = "payment-success";
    public static final String TEMPLATE_PAYMENT_FAILED = "payment-failed";
    public static final String TEMPLATE_BOOKING_REMINDER = "booking-reminder";
}
