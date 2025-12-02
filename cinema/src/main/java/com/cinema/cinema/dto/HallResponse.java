package com.cinema.cinema.dto;

import com.cinema.cinema.entity.Hall;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HallResponse {
    private Long id;
    private Long cinemaId;
    private String cinemaName;
    private String name;
    private String hallType;
    private Integer totalRows;
    private Integer seatsPerRow;
    private Integer totalSeats;
    private String status;
    private List<SeatResponse> seats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static HallResponse fromEntity(Hall hall) {
        return HallResponse.builder()
                .id(hall.getId())
                .cinemaId(hall.getCinema() != null ? hall.getCinema().getId() : null)
                .cinemaName(hall.getCinema() != null ? hall.getCinema().getName() : null)
                .name(hall.getName())
                .hallType(hall.getHallType().name())
                .totalRows(hall.getTotalRows())
                .seatsPerRow(hall.getSeatsPerRow())
                .totalSeats(hall.getTotalSeats())
                .status(hall.getStatus().name())
                .createdAt(hall.getCreatedAt())
                .updatedAt(hall.getUpdatedAt())
                .build();
    }

    public static HallResponse fromEntityWithSeats(Hall hall) {
        HallResponse response = fromEntity(hall);
        if (hall.getSeats() != null) {
            response.setSeats(hall.getSeats().stream()
                    .map(SeatResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        return response;
    }
}
