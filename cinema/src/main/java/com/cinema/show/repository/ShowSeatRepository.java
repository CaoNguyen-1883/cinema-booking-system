package com.cinema.show.repository;

import com.cinema.show.entity.ShowSeat;
import com.cinema.show.entity.ShowSeat.ShowSeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    // Find all seats for a show
    @Query("SELECT ss FROM ShowSeat ss " +
           "JOIN FETCH ss.seat s " +
           "WHERE ss.show.id = :showId " +
           "ORDER BY s.rowName, s.seatNumber")
    List<ShowSeat> findByShowIdWithSeat(@Param("showId") Long showId);

    // Find specific seat for a show
    @Query("SELECT ss FROM ShowSeat ss " +
           "JOIN FETCH ss.seat s " +
           "WHERE ss.show.id = :showId AND ss.seat.id = :seatId")
    Optional<ShowSeat> findByShowIdAndSeatId(
            @Param("showId") Long showId,
            @Param("seatId") Long seatId);

    // Find multiple seats by IDs
    @Query("SELECT ss FROM ShowSeat ss " +
           "JOIN FETCH ss.seat s " +
           "WHERE ss.show.id = :showId AND ss.seat.id IN :seatIds")
    List<ShowSeat> findByShowIdAndSeatIds(
            @Param("showId") Long showId,
            @Param("seatIds") List<Long> seatIds);

    // Count available seats for a show
    long countByShowIdAndStatus(Long showId, ShowSeatStatus status);

    // Find expired locked seats
    @Query("SELECT ss FROM ShowSeat ss " +
           "WHERE ss.status = 'LOCKED' " +
           "AND ss.lockedAt < :expireTime")
    List<ShowSeat> findExpiredLockedSeats(@Param("expireTime") LocalDateTime expireTime);

    // Find seats locked by a specific user
    @Query("SELECT ss FROM ShowSeat ss " +
           "JOIN FETCH ss.seat s " +
           "WHERE ss.show.id = :showId " +
           "AND ss.lockedBy.id = :userId " +
           "AND ss.status = 'LOCKED'")
    List<ShowSeat> findLockedSeatsByUser(
            @Param("showId") Long showId,
            @Param("userId") Long userId);

    // Delete all show seats for a show
    @Modifying
    @Query("DELETE FROM ShowSeat ss WHERE ss.show.id = :showId")
    void deleteByShowId(@Param("showId") Long showId);

    // Unlock expired seats
    @Modifying
    @Query("UPDATE ShowSeat ss SET ss.status = 'AVAILABLE', ss.lockedBy = null, ss.lockedAt = null " +
           "WHERE ss.status = 'LOCKED' AND ss.lockedAt < :expireTime")
    int unlockExpiredSeats(@Param("expireTime") LocalDateTime expireTime);

    // Check if show has any sold seats
    boolean existsByShowIdAndStatus(Long showId, ShowSeatStatus status);
}
