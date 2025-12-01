package com.cinema.show.entity;

import com.cinema.cinema.entity.Hall;
import com.cinema.movie.entity.Movie;
import com.cinema.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Show extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(name = "show_date", nullable = false)
    private LocalDate showDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ShowStatus status = ShowStatus.SCHEDULED;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ShowSeat> showSeats = new ArrayList<>();

    public enum ShowStatus {
        SCHEDULED, ONGOING, COMPLETED, CANCELLED
    }

    public boolean isBookable() {
        return status == ShowStatus.SCHEDULED;
    }

    public long getAvailableSeatsCount() {
        return showSeats.stream()
            .filter(ss -> ss.getStatus() == ShowSeat.ShowSeatStatus.AVAILABLE)
            .count();
    }
}
