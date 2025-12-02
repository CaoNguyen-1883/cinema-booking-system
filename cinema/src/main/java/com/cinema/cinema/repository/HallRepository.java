package com.cinema.cinema.repository;

import com.cinema.cinema.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {

    List<Hall> findByCinemaId(Long cinemaId);

    List<Hall> findByCinemaIdAndStatus(Long cinemaId, Hall.HallStatus status);

    @Query("SELECT h FROM Hall h LEFT JOIN FETCH h.seats WHERE h.id = :id")
    Optional<Hall> findByIdWithSeats(@Param("id") Long id);

    @Query("SELECT h FROM Hall h WHERE h.cinema.id = :cinemaId AND h.status = 'ACTIVE' ORDER BY h.name")
    List<Hall> findActiveHallsByCinemaId(@Param("cinemaId") Long cinemaId);

    boolean existsByCinemaIdAndName(Long cinemaId, String name);

    @Query("SELECT COUNT(h) FROM Hall h WHERE h.cinema.id = :cinemaId")
    int countByCinemaId(@Param("cinemaId") Long cinemaId);
}
