package com.cinema.cinema.service;

import com.cinema.cinema.dto.*;
import com.cinema.cinema.entity.Cinema;
import com.cinema.cinema.entity.Cinema.CinemaStatus;
import com.cinema.cinema.repository.CinemaRepository;
import com.cinema.shared.config.RedisConfig;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CinemaService {

    private final CinemaRepository cinemaRepository;

    @Cacheable(value = RedisConfig.CACHE_CINEMA_DETAIL, key = "#id")
    public CinemaResponse getCinemaById(Long id) {
        log.debug("Fetching cinema {} from database", id);
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CINEMA_NOT_FOUND));
        return CinemaResponse.fromEntity(cinema);
    }

    @Cacheable(value = RedisConfig.CACHE_CINEMA_DETAIL, key = "'withHalls:' + #id")
    public CinemaResponse getCinemaByIdWithHalls(Long id) {
        log.debug("Fetching cinema {} with halls from database", id);
        Cinema cinema = cinemaRepository.findByIdWithHalls(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CINEMA_NOT_FOUND));
        return CinemaResponse.fromEntityWithHalls(cinema);
    }

    public Page<CinemaResponse> getAllCinemas(Pageable pageable) {
        return cinemaRepository.findAll(pageable)
                .map(CinemaResponse::fromEntity);
    }

    public Page<CinemaResponse> getCinemasByStatus(String status, Pageable pageable) {
        CinemaStatus cinemaStatus = CinemaStatus.valueOf(status.toUpperCase());
        return cinemaRepository.findByStatus(cinemaStatus, pageable)
                .map(CinemaResponse::fromEntity);
    }

    public Page<CinemaResponse> getCinemasByCity(String city, Pageable pageable) {
        return cinemaRepository.findByCity(city, pageable)
                .map(CinemaResponse::fromEntity);
    }

    public Page<CinemaResponse> getCinemasByCityAndStatus(String city, String status, Pageable pageable) {
        CinemaStatus cinemaStatus = CinemaStatus.valueOf(status.toUpperCase());
        return cinemaRepository.findByCityAndStatus(city, cinemaStatus, pageable)
                .map(CinemaResponse::fromEntity);
    }

    @Cacheable(value = RedisConfig.CACHE_CINEMAS, key = "'active'")
    public List<CinemaResponse> getActiveCinemas() {
        log.debug("Fetching active cinemas from database");
        return cinemaRepository.findByStatusOrderByNameAsc(CinemaStatus.ACTIVE)
                .stream()
                .map(CinemaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = RedisConfig.CACHE_CINEMAS, key = "'byCity'")
    public List<CinemaResponse> getActiveCinemasGroupedByCity() {
        log.debug("Fetching active cinemas grouped by city from database");
        return cinemaRepository.findAllActiveCinemasGroupedByCity()
                .stream()
                .map(CinemaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = RedisConfig.CACHE_CINEMAS, key = "'cities'")
    public List<String> getDistinctCities() {
        log.debug("Fetching distinct cities from database");
        return cinemaRepository.findDistinctCities();
    }

    public Page<CinemaResponse> searchCinemas(String keyword, Pageable pageable) {
        return cinemaRepository.searchByKeyword(keyword, pageable)
                .map(CinemaResponse::fromEntity);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_CINEMAS, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_CINEMA_DETAIL, allEntries = true)
    })
    public CinemaResponse createCinema(CreateCinemaRequest request) {
        if (cinemaRepository.existsByNameAndCity(request.getName(), request.getCity())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
        }

        Cinema cinema = Cinema.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .district(request.getDistrict())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .openingHours(request.getOpeningHours() != null ? request.getOpeningHours() : "08:00-24:00")
                .facilities(request.getFacilities())
                .status(CinemaStatus.ACTIVE)
                .build();

        Cinema savedCinema = cinemaRepository.save(cinema);
        log.info("Created cinema: {} (ID: {})", savedCinema.getName(), savedCinema.getId());
        return CinemaResponse.fromEntity(savedCinema);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_CINEMAS, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_CINEMA_DETAIL, key = "#id"),
        @CacheEvict(value = RedisConfig.CACHE_CINEMA_DETAIL, key = "'withHalls:' + #id")
    })
    public CinemaResponse updateCinema(Long id, UpdateCinemaRequest request) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CINEMA_NOT_FOUND));

        if (request.getName() != null) cinema.setName(request.getName());
        if (request.getAddress() != null) cinema.setAddress(request.getAddress());
        if (request.getCity() != null) cinema.setCity(request.getCity());
        if (request.getDistrict() != null) cinema.setDistrict(request.getDistrict());
        if (request.getPhoneNumber() != null) cinema.setPhoneNumber(request.getPhoneNumber());
        if (request.getEmail() != null) cinema.setEmail(request.getEmail());
        if (request.getOpeningHours() != null) cinema.setOpeningHours(request.getOpeningHours());
        if (request.getFacilities() != null) cinema.setFacilities(request.getFacilities());
        if (request.getStatus() != null) {
            cinema.setStatus(CinemaStatus.valueOf(request.getStatus().toUpperCase()));
        }

        Cinema updatedCinema = cinemaRepository.save(cinema);
        log.info("Updated cinema: {} (ID: {})", updatedCinema.getName(), updatedCinema.getId());
        return CinemaResponse.fromEntity(updatedCinema);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_CINEMAS, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_CINEMA_DETAIL, key = "#id"),
        @CacheEvict(value = RedisConfig.CACHE_CINEMA_DETAIL, key = "'withHalls:' + #id")
    })
    public void deleteCinema(Long id) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CINEMA_NOT_FOUND));
        cinemaRepository.delete(cinema);
        log.info("Deleted cinema: {} (ID: {})", cinema.getName(), id);
    }
}
