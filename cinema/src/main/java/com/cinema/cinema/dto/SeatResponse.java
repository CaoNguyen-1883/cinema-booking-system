package com.cinema.cinema.dto;

import com.cinema.cinema.entity.Seat;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    private Long id;
    private Long hallId;
    private String rowName;
    private Integer seatNumber;
    private String seatLabel;
    private String seatType;
    private String status;

    public static SeatResponse fromEntity(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .hallId(seat.getHall() != null ? seat.getHall().getId() : null)
                .rowName(seat.getRowName())
                .seatNumber(seat.getSeatNumber())
                .seatLabel(seat.getSeatLabel())
                .seatType(seat.getSeatType().name())
                .status(seat.getStatus().name())
                .build();
    }
}
