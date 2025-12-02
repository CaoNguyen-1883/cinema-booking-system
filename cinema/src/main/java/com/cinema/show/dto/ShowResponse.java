package com.cinema.show.dto;

import com.cinema.show.entity.Show;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowResponse {
    private Long id;
    private LocalDate showDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal basePrice;
    private String status;
    private long availableSeats;
    private long totalSeats;

    // Movie info
    private Long movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private Integer movieDuration;
    private String movieRating;

    // Hall info
    private Long hallId;
    private String hallName;
    private String hallType;

    // Cinema info
    private Long cinemaId;
    private String cinemaName;
    private String cinemaCity;

    // Seats (only when requested)
    private List<ShowSeatResponse> seats;

    private LocalDateTime createdAt;

    public static ShowResponse fromEntity(Show show) {
        return ShowResponse.builder()
                .id(show.getId())
                .showDate(show.getShowDate())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .basePrice(show.getBasePrice())
                .status(show.getStatus().name())
                .availableSeats(show.getAvailableSeatsCount())
                .totalSeats(show.getShowSeats() != null ? show.getShowSeats().size() : 0)
                .movieId(show.getMovie().getId())
                .movieTitle(show.getMovie().getTitle())
                .moviePosterUrl(show.getMovie().getPosterUrl())
                .movieDuration(show.getMovie().getDuration())
                .movieRating(show.getMovie().getRating() != null ? show.getMovie().getRating().name() : null)
                .hallId(show.getHall().getId())
                .hallName(show.getHall().getName())
                .hallType(show.getHall().getHallType().name())
                .cinemaId(show.getHall().getCinema().getId())
                .cinemaName(show.getHall().getCinema().getName())
                .cinemaCity(show.getHall().getCinema().getCity())
                .createdAt(show.getCreatedAt())
                .build();
    }

    public static ShowResponse fromEntityWithSeats(Show show) {
        ShowResponse response = fromEntity(show);
        if (show.getShowSeats() != null) {
            response.setSeats(show.getShowSeats().stream()
                    .map(ShowSeatResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    public static ShowResponse fromEntityBasic(Show show) {
        return ShowResponse.builder()
                .id(show.getId())
                .showDate(show.getShowDate())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .basePrice(show.getBasePrice())
                .status(show.getStatus().name())
                .hallId(show.getHall().getId())
                .hallName(show.getHall().getName())
                .hallType(show.getHall().getHallType().name())
                .build();
    }
}
