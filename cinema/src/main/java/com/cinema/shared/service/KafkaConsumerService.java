package com.cinema.shared.service;

import com.cinema.shared.config.KafkaConfig;
import com.cinema.shared.event.BookingEvent;
import com.cinema.shared.event.NotificationEvent;
import com.cinema.shared.event.PaymentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Kafka Consumer Service for processing booking, payment and notification events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    // ==================== Booking Events ====================

    @KafkaListener(
            topics = KafkaConfig.TOPIC_BOOKING_CREATED,
            groupId = "booking-processor-group"
    )
    public void handleBookingCreated(BookingEvent event) {
        log.info("Received booking created event: bookingCode={}, userId={}",
                event.getBooking().getBookingCode(),
                event.getUser().getUserId());

        // Process booking created event
        // - Update analytics (could send to analytics service)
        // - Send real-time updates via WebSocket (future enhancement)
        log.info("Booking {} created for user {}, expires at {}",
                event.getBooking().getBookingCode(),
                event.getUser().getUsername(),
                event.getBooking().getExpiresAt());
    }

    @KafkaListener(
            topics = KafkaConfig.TOPIC_BOOKING_CONFIRMED,
            groupId = "booking-processor-group"
    )
    public void handleBookingConfirmed(BookingEvent event) {
        log.info("Received booking confirmed event: bookingCode={}, qrCode={}",
                event.getBooking().getBookingCode(),
                event.getBooking().getQrCode() != null ? "present" : "null");

        // Process booking confirmed event
        // - Log for analytics
        log.info("Booking {} confirmed. Movie: {}, Show: {} {}",
                event.getBooking().getBookingCode(),
                event.getBooking().getShow() != null ? event.getBooking().getShow().getMovieTitle() : "N/A",
                event.getBooking().getShow() != null ? event.getBooking().getShow().getShowDate() : "N/A",
                event.getBooking().getShow() != null ? event.getBooking().getShow().getStartTime() : "N/A");
    }

    @KafkaListener(
            topics = KafkaConfig.TOPIC_BOOKING_CANCELLED,
            groupId = "booking-processor-group"
    )
    public void handleBookingCancelled(BookingEvent event) {
        log.info("Received booking cancelled event: bookingCode={}",
                event.getBooking().getBookingCode());

        // Process booking cancelled event
        // - Update seat availability (already done in service)
        // - Process refund if applicable (future enhancement)
        log.info("Booking {} cancelled for user {}",
                event.getBooking().getBookingCode(),
                event.getUser().getUsername());
    }

    @KafkaListener(
            topics = KafkaConfig.TOPIC_BOOKING_EXPIRED,
            groupId = "booking-processor-group"
    )
    public void handleBookingExpired(BookingEvent event) {
        log.info("Received booking expired event: bookingCode={}",
                event.getBooking().getBookingCode());

        // Process booking expired event
        // - Clean up resources (already done in scheduler)
        log.info("Booking {} expired. User: {}",
                event.getBooking().getBookingCode(),
                event.getUser().getUsername());
    }

    // ==================== Payment Events ====================

    @KafkaListener(
            topics = KafkaConfig.TOPIC_PAYMENT_COMPLETED,
            groupId = "payment-processor-group"
    )
    public void handlePaymentCompleted(PaymentEvent event) {
        log.info("Received payment completed event: bookingCode={}, transactionId={}, amount={}",
                event.getBooking().getBookingCode(),
                event.getPayment().getTransactionId(),
                event.getPayment().getAmount());

        // Process payment completed event
        // - Update revenue reports (future enhancement)
        // - Analytics tracking
        log.info("Payment {} completed for booking {}. Method: {}, Amount: {}",
                event.getPayment().getPaymentId(),
                event.getBooking().getBookingCode(),
                event.getPayment().getPaymentMethod(),
                event.getPayment().getAmount());
    }

    @KafkaListener(
            topics = KafkaConfig.TOPIC_PAYMENT_FAILED,
            groupId = "payment-processor-group"
    )
    public void handlePaymentFailed(PaymentEvent event) {
        log.info("Received payment failed event: bookingCode={}, reason={}",
                event.getBooking().getBookingCode(),
                event.getPayment().getFailureReason());

        // Process payment failed event
        // - Track failure reasons for analytics
        // - Alert if too many failures
        log.warn("Payment failed for booking {}. Reason: {}",
                event.getBooking().getBookingCode(),
                event.getPayment().getFailureReason());
    }

    // ==================== Notification Events ====================

    @KafkaListener(
            topics = KafkaConfig.TOPIC_NOTIFICATION,
            groupId = "notification-processor-group"
    )
    public void handleNotification(NotificationEvent event) {
        log.info("Received notification event: type={}, template={}, recipient={}",
                event.getType(),
                event.getTemplateName(),
                event.getRecipient().getEmail());

        try {
            switch (event.getType()) {
                case EMAIL:
                    processEmailNotification(event);
                    break;
                case SMS:
                    processSmsNotification(event);
                    break;
                case PUSH:
                    processPushNotification(event);
                    break;
                default:
                    log.warn("Unknown notification type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error processing notification: {}", e.getMessage(), e);
        }
    }

    private void processEmailNotification(NotificationEvent event) {
        log.info("Processing email notification to: {}, template: {}",
                event.getRecipient().getEmail(),
                event.getTemplateName());

        Map<String, Object> data = event.getData();
        String email = event.getRecipient().getEmail();

        try {
            switch (event.getTemplateName()) {
                case NotificationEvent.TEMPLATE_BOOKING_CREATED:
                    emailService.sendBookingCreated(
                            email,
                            getStringValue(data, "customerName", "Quý khách"),
                            getStringValue(data, "bookingCode", ""),
                            getStringValue(data, "movieTitle", ""),
                            getStringValue(data, "showDate", ""),
                            getStringValue(data, "showTime", ""),
                            getStringValue(data, "totalAmount", "0"),
                            15 // expiration minutes
                    );
                    log.info("Sent booking created email to {}", email);
                    break;

                case NotificationEvent.TEMPLATE_BOOKING_CONFIRMED:
                    // This is handled directly in PaymentService with more data
                    log.info("Booking confirmed notification received - email already sent by PaymentService");
                    break;

                case NotificationEvent.TEMPLATE_BOOKING_CANCELLED:
                    emailService.sendBookingCancelled(
                            email,
                            getStringValue(data, "customerName", "Quý khách"),
                            getStringValue(data, "bookingCode", ""),
                            getStringValue(data, "movieTitle", ""),
                            getStringValue(data, "message", "Theo yêu cầu của khách hàng")
                    );
                    log.info("Sent booking cancelled email to {}", email);
                    break;

                case NotificationEvent.TEMPLATE_BOOKING_EXPIRED:
                    emailService.sendBookingExpired(
                            email,
                            getStringValue(data, "customerName", "Quý khách"),
                            getStringValue(data, "bookingCode", ""),
                            getStringValue(data, "movieTitle", "")
                    );
                    log.info("Sent booking expired email to {}", email);
                    break;

                case NotificationEvent.TEMPLATE_PAYMENT_SUCCESS:
                    // Payment success email is already sent directly in PaymentService
                    log.info("Payment success notification received - email already sent by PaymentService");
                    break;

                case NotificationEvent.TEMPLATE_PAYMENT_FAILED:
                    // Send payment failed notification
                    emailService.sendSimpleEmail(
                            email,
                            "Thanh toán thất bại - " + getStringValue(data, "bookingCode", ""),
                            "Xin chào,\n\n" +
                            "Thanh toán cho đặt vé " + getStringValue(data, "bookingCode", "") + " đã thất bại.\n" +
                            "Lý do: " + getStringValue(data, "message", "Không xác định") + "\n\n" +
                            "Vui lòng thử lại hoặc liên hệ hỗ trợ.\n\n" +
                            "Trân trọng,\nCinema Booking"
                    );
                    log.info("Sent payment failed email to {}", email);
                    break;

                case NotificationEvent.TEMPLATE_BOOKING_REMINDER:
                    // Reminder before showtime
                    emailService.sendSimpleEmail(
                            email,
                            "Nhắc nhở - Suất chiếu sắp bắt đầu",
                            "Xin chào,\n\n" +
                            "Suất chiếu của bạn sắp bắt đầu!\n" +
                            "Mã đặt vé: " + getStringValue(data, "bookingCode", "") + "\n" +
                            "Phim: " + getStringValue(data, "movieTitle", "") + "\n" +
                            "Thời gian: " + getStringValue(data, "showDate", "") + " " + getStringValue(data, "showTime", "") + "\n\n" +
                            "Vui lòng đến rạp trước 15 phút.\n\n" +
                            "Trân trọng,\nCinema Booking"
                    );
                    log.info("Sent booking reminder email to {}", email);
                    break;

                default:
                    log.warn("Unknown email template: {}", event.getTemplateName());
            }
        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage(), e);
        }
    }

    private void processSmsNotification(NotificationEvent event) {
        log.info("Processing SMS notification to: {}", event.getRecipient().getPhoneNumber());
        // TODO: Implement SMS sending via provider (Twilio, etc.)
        // For now, just log
        log.info("SMS would be sent to {} with template {}",
                event.getRecipient().getPhoneNumber(),
                event.getTemplateName());
    }

    private void processPushNotification(NotificationEvent event) {
        log.info("Processing push notification to device: {}", event.getRecipient().getDeviceToken());
        // TODO: Implement push notification via Firebase/APNs
        // For now, just log
        log.info("Push notification would be sent to device {} with template {}",
                event.getRecipient().getDeviceToken(),
                event.getTemplateName());
    }

    /**
     * Helper method to safely get string value from map
     */
    private String getStringValue(Map<String, Object> data, String key, String defaultValue) {
        if (data == null) return defaultValue;
        Object value = data.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}
