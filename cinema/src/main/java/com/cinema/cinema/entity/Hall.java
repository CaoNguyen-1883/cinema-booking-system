package com.cinema.cinema.entity;

import com.cinema.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "halls", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cinema_id", "name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "hall_type", nullable = false, length = 20)
    @Builder.Default
    private HallType hallType = HallType.STANDARD;

    @Column(name = "total_rows", nullable = false)
    private Integer totalRows;

    @Column(name = "seats_per_row", nullable = false)
    private Integer seatsPerRow;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private HallStatus status = HallStatus.ACTIVE;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();

    public enum HallType {
        STANDARD, VIP, IMAX, THREE_D, FOUR_DX
    }

    public enum HallStatus {
        ACTIVE, INACTIVE, MAINTENANCE
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.setHall(this);
    }
}
