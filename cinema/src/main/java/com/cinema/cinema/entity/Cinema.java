package com.cinema.cinema.entity;

import com.cinema.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cinemas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cinema extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String district;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    private String email;

    @Column(name = "opening_hours", length = 100)
    @Builder.Default
    private String openingHours = "08:00-24:00";

    @Column(columnDefinition = "TEXT")
    private String facilities;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CinemaStatus status = CinemaStatus.ACTIVE;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Hall> halls = new ArrayList<>();

    public enum CinemaStatus {
        ACTIVE, INACTIVE, MAINTENANCE
    }

    public void addHall(Hall hall) {
        halls.add(hall);
        hall.setCinema(this);
    }

    public void removeHall(Hall hall) {
        halls.remove(hall);
        hall.setCinema(null);
    }
}
