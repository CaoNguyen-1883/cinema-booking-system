package com.cinema.shared.service;

import com.cinema.booking.entity.Booking;
import com.cinema.shared.config.KafkaConfig;
import com.cinema.shared.event.BookingEvent;
import com.cinema.shared.event.NotificationEvent;
import com.cinema.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Kafka Producer Service for publishing booking events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publish booking created event
     */
    public void publishBookingCreated(Booking booking) {
        BookingEvent event = buildBookingEvent(booking, BookingEvent.TYPE_CREATED);
        sendEvent(KafkaConfig.TOPIC_BOOKING_CREATED, booking.getBookingCode(), event);

        // Also send notification event
        sendBookingNotification(booking, NotificationEvent.TEMPLATE_BOOKING_CREATED,
                "Đặt vé thành công! Vui lòng thanh toán trong 15 phút.");
    }

    /**
     * Publish booking confirmed event
     */
    public void publishBookingConfirmed(Booking booking) {
        BookingEvent event = buildBookingEvent(booking, BookingEvent.TYPE_CONFIRMED);
        sendEvent(KafkaConfig.TOPIC_BOOKING_CONFIRMED, booking.getBookingCode(), event);

        // Also send notification event
        sendBookingNotification(booking, NotificationEvent.TEMPLATE_BOOKING_CONFIRMED,
                "Thanh toán thành công! Mã QR đã được tạo.");
    }

    /**
     * Publish booking cancelled event
     */
    public void publishBookingCancelled(Booking booking) {
        BookingEvent event = buildBookingEvent(booking, BookingEvent.TYPE_CANCELLED);
        sendEvent(KafkaConfig.TOPIC_BOOKING_CANCELLED, booking.getBookingCode(), event);

        // Also send notification event
        sendBookingNotification(booking, NotificationEvent.TEMPLATE_BOOKING_CANCELLED,
                "Đặt vé đã bị hủy.");
    }

    /**
     * Publish booking expired event
     */
    public void publishBookingExpired(Booking booking) {
        BookingEvent event = buildBookingEvent(booking, BookingEvent.TYPE_EXPIRED);
        sendEvent(KafkaConfig.TOPIC_BOOKING_EXPIRED, booking.getBookingCode(), event);

        // Also send notification event
        sendBookingNotification(booking, NotificationEvent.TEMPLATE_BOOKING_EXPIRED,
                "Đặt vé đã hết hạn do không thanh toán kịp thời.");
    }

    /**
     * Send notification event
     */
    private void sendBookingNotification(Booking booking, String template, String message) {
        User user = booking.getUser();

        Map<String, Object> data = new HashMap<>();
        data.put("bookingCode", booking.getBookingCode());
        data.put("message", message);
        data.put("movieTitle", booking.getShow() != null && booking.getShow().getMovie() != null ?
                booking.getShow().getMovie().getTitle() : "N/A");
        data.put("showDate", booking.getShow() != null ? booking.getShow().getShowDate().toString() : "N/A");
        data.put("showTime", booking.getShow() != null ? booking.getShow().getStartTime().toString() : "N/A");
        data.put("totalAmount", booking.getFinalAmount());
        data.put("qrCode", booking.getQrCode());

        NotificationEvent notification = NotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .type(NotificationEvent.NotificationType.EMAIL)
                .templateName(template)
                .recipient(NotificationEvent.RecipientData.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .build())
                .data(data)
                .build();

        sendEvent(KafkaConfig.TOPIC_NOTIFICATION, user.getId().toString(), notification);
    }

    /**
     * Build booking event from entity
     */
    private BookingEvent buildBookingEvent(Booking booking, String eventType) {
        User user = booking.getUser();

        // Build show data
        BookingEvent.ShowData showData = null;
        if (booking.getShow() != null) {
            var show = booking.getShow();
            showData = BookingEvent.ShowData.builder()
                    .showId(show.getId())
                    .movieTitle(show.getMovie() != null ? show.getMovie().getTitle() : null)
                    .cinemaName(show.getHall() != null && show.getHall().getCinema() != null ?
                            show.getHall().getCinema().getName() : null)
                    .hallName(show.getHall() != null ? show.getHall().getName() : null)
                    .showDate(show.getShowDate() != null ? show.getShowDate().toString() : null)
                    .startTime(show.getStartTime() != null ? show.getStartTime().toString() : null)
                    .build();
        }

        // Build seats data
        List<BookingEvent.SeatData> seatsData = null;
        if (booking.getBookingSeats() != null && !booking.getBookingSeats().isEmpty()) {
            seatsData = booking.getBookingSeats().stream()
                    .map(bs -> BookingEvent.SeatData.builder()
                            .showSeatId(bs.getShowSeat().getId())
                            .seatCode(bs.getShowSeat().getSeat() != null ?
                                    bs.getShowSeat().getSeat().getRowName() +
                                    bs.getShowSeat().getSeat().getSeatNumber() : null)
                            .seatType(bs.getShowSeat().getSeat() != null ?
                                    bs.getShowSeat().getSeat().getSeatType().name() : null)
                            .price(bs.getPrice())
                            .build())
                    .collect(Collectors.toList());
        }

        // Build booking data
        BookingEvent.BookingData bookingData = BookingEvent.BookingData.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .status(booking.getStatus().name())
                .totalAmount(booking.getTotalAmount())
                .finalAmount(booking.getFinalAmount())
                .pointsUsed(booking.getPointsUsed())
                .pointsEarned(booking.getPointsEarned())
                .qrCode(booking.getQrCode())
                .expiresAt(booking.getExpiresAt())
                .show(showData)
                .seats(seatsData)
                .build();

        // Build user data
        BookingEvent.UserData userData = BookingEvent.UserData.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .build();

        return BookingEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .booking(bookingData)
                .user(userData)
                .build();
    }

    /**
     * Generic method to send event to Kafka
     */
    private void sendEvent(String topic, String key, Object event) {
        try {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(topic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent event to topic {} with key {}: partition={}, offset={}",
                            topic, key,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send event to topic {} with key {}: {}",
                            topic, key, ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error sending event to topic {}: {}", topic, e.getMessage());
        }
    }
}
