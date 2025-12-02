package com.cinema.booking.dto;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.Booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private String bookingCode;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private Integer pointsUsed;
    private Integer pointsEarned;
    private String qrCode;
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;

    // Show info
    private ShowInfo show;

    // Seats
    private List<SeatInfo> seats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowInfo {
        private Long id;
        private String movieTitle;
        private String posterUrl;
        private String cinemaName;
        private String hallName;
        private String showDate;
        private String startTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatInfo {
        private Long showSeatId;
        private String seatCode;
        private String seatType;
        private BigDecimal price;
    }

    public static BookingResponse fromEntity(Booking booking) {
        BookingResponseBuilder builder = BookingResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .discountAmount(booking.getDiscountAmount())
                .finalAmount(booking.getFinalAmount())
                .pointsUsed(booking.getPointsUsed())
                .pointsEarned(booking.getPointsEarned())
                .qrCode(booking.getQrCode())
                .expiresAt(booking.getExpiresAt())
                .confirmedAt(booking.getConfirmedAt())
                .cancelledAt(booking.getCancelledAt())
                .createdAt(booking.getCreatedAt());

        // Show info
        if (booking.getShow() != null) {
            var show = booking.getShow();
            ShowInfo showInfo = ShowInfo.builder()
                    .id(show.getId())
                    .movieTitle(show.getMovie() != null ? show.getMovie().getTitle() : null)
                    .posterUrl(show.getMovie() != null ? show.getMovie().getPosterUrl() : null)
                    .cinemaName(show.getHall() != null && show.getHall().getCinema() != null ?
                            show.getHall().getCinema().getName() : null)
                    .hallName(show.getHall() != null ? show.getHall().getName() : null)
                    .showDate(show.getShowDate() != null ? show.getShowDate().toString() : null)
                    .startTime(show.getStartTime() != null ? show.getStartTime().toString() : null)
                    .build();
            builder.show(showInfo);
        }

        // Seats
        if (booking.getBookingSeats() != null && !booking.getBookingSeats().isEmpty()) {
            List<SeatInfo> seats = booking.getBookingSeats().stream()
                    .map(bs -> SeatInfo.builder()
                            .showSeatId(bs.getShowSeat().getId())
                            .seatCode(bs.getShowSeat().getSeat() != null ?
                                    bs.getShowSeat().getSeat().getRowName() + bs.getShowSeat().getSeat().getSeatNumber() : null)
                            .seatType(bs.getShowSeat().getSeat() != null ?
                                    bs.getShowSeat().getSeat().getSeatType().name() : null)
                            .price(bs.getPrice())
                            .build())
                    .collect(Collectors.toList());
            builder.seats(seats);
        }

        return builder.build();
    }

    public static BookingResponse fromEntityBasic(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .finalAmount(booking.getFinalAmount())
                .expiresAt(booking.getExpiresAt())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
