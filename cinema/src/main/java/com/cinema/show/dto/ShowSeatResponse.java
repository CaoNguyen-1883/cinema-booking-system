package com.cinema.show.dto;

import com.cinema.show.entity.ShowSeat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeatResponse {
    private Long id;
    private Long seatId;
    private String rowName;
    private Integer seatNumber;
    private String seatLabel;
    private String seatType;
    private BigDecimal price;
    private String status;

    public static ShowSeatResponse fromEntity(ShowSeat showSeat) {
        return ShowSeatResponse.builder()
                .id(showSeat.getId())
                .seatId(showSeat.getSeat().getId())
                .rowName(showSeat.getSeat().getRowName())
                .seatNumber(showSeat.getSeat().getSeatNumber())
                .seatLabel(showSeat.getSeat().getSeatLabel())
                .seatType(showSeat.getSeat().getSeatType().name())
                .price(showSeat.getPrice())
                .status(showSeat.getStatus().name())
                .build();
    }
}
