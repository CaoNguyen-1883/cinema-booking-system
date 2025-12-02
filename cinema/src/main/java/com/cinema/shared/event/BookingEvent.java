package com.cinema.shared.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Base booking event for Kafka messaging
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private BookingData booking;
    private UserData user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingData {
        private Long bookingId;
        private String bookingCode;
        private String status;
        private BigDecimal totalAmount;
        private BigDecimal finalAmount;
        private Integer pointsUsed;
        private Integer pointsEarned;
        private String qrCode;
        private LocalDateTime expiresAt;
        private ShowData show;
        private List<SeatData> seats;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowData {
        private Long showId;
        private String movieTitle;
        private String cinemaName;
        private String hallName;
        private String showDate;
        private String startTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatData {
        private Long showSeatId;
        private String seatCode;
        private String seatType;
        private BigDecimal price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserData {
        private Long userId;
        private String username;
        private String email;
        private String fullName;
        private String phoneNumber;
    }

    // Event types
    public static final String TYPE_CREATED = "BOOKING_CREATED";
    public static final String TYPE_CONFIRMED = "BOOKING_CONFIRMED";
    public static final String TYPE_CANCELLED = "BOOKING_CANCELLED";
    public static final String TYPE_EXPIRED = "BOOKING_EXPIRED";
}
