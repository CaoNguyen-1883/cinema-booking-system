package com.cinema.cinema.repository;

import com.cinema.cinema.entity.Cinema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {

    Page<Cinema> findByStatus(Cinema.CinemaStatus status, Pageable pageable);

    Page<Cinema> findByCity(String city, Pageable pageable);

    Page<Cinema> findByCityAndStatus(String city, Cinema.CinemaStatus status, Pageable pageable);

    List<Cinema> findByStatusOrderByNameAsc(Cinema.CinemaStatus status);

    @Query("SELECT c FROM Cinema c WHERE c.status = 'ACTIVE' ORDER BY c.city, c.name")
    List<Cinema> findAllActiveCinemasGroupedByCity();

    @Query("SELECT DISTINCT c.city FROM Cinema c WHERE c.status = 'ACTIVE' ORDER BY c.city")
    List<String> findDistinctCities();

    @Query("SELECT c FROM Cinema c LEFT JOIN FETCH c.halls WHERE c.id = :id")
    Optional<Cinema> findByIdWithHalls(@Param("id") Long id);

    @Query("SELECT c FROM Cinema c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Cinema> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByNameAndCity(String name, String city);
}
