package com.cinema.show.entity;

import com.cinema.cinema.entity.Seat;
import com.cinema.shared.entity.BaseEntity;
import com.cinema.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "show_seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"show_id", "seat_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ShowSeatStatus status = ShowSeatStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locked_by")
    private User lockedBy;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    public enum ShowSeatStatus {
        AVAILABLE, LOCKED, SOLD
    }

    public boolean isAvailable() {
        return status == ShowSeatStatus.AVAILABLE;
    }

    public boolean isLocked() {
        return status == ShowSeatStatus.LOCKED;
    }

    public boolean isSold() {
        return status == ShowSeatStatus.SOLD;
    }

    public void lock(User user) {
        this.status = ShowSeatStatus.LOCKED;
        this.lockedBy = user;
        this.lockedAt = LocalDateTime.now();
    }

    public void unlock() {
        this.status = ShowSeatStatus.AVAILABLE;
        this.lockedBy = null;
        this.lockedAt = null;
    }

    public void sell() {
        this.status = ShowSeatStatus.SOLD;
    }
}
