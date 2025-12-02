package com.cinema.cinema.repository;

import com.cinema.cinema.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByHallId(Long hallId);

    List<Seat> findByHallIdAndStatus(Long hallId, Seat.SeatStatus status);

    @Query("SELECT s FROM Seat s WHERE s.hall.id = :hallId ORDER BY s.rowName, s.seatNumber")
    List<Seat> findByHallIdOrderBySeat(@Param("hallId") Long hallId);

    Optional<Seat> findByHallIdAndRowNameAndSeatNumber(Long hallId, String rowName, Integer seatNumber);

    @Query("SELECT s FROM Seat s WHERE s.hall.id = :hallId AND s.status = 'ACTIVE' ORDER BY s.rowName, s.seatNumber")
    List<Seat> findActiveSeatsbyHallId(@Param("hallId") Long hallId);

    @Modifying
    @Query("DELETE FROM Seat s WHERE s.hall.id = :hallId")
    void deleteByHallId(@Param("hallId") Long hallId);

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.hall.id = :hallId AND s.status = 'ACTIVE'")
    int countActiveSeatsbyHallId(@Param("hallId") Long hallId);
}
