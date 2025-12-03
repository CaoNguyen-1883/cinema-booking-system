package com.cinema.cinema.service;

import com.cinema.cinema.dto.*;
import com.cinema.cinema.entity.Cinema;
import com.cinema.cinema.entity.Hall;
import com.cinema.cinema.entity.Hall.HallStatus;
import com.cinema.cinema.entity.Hall.HallType;
import com.cinema.cinema.entity.Seat;
import com.cinema.cinema.entity.Seat.SeatType;
import com.cinema.cinema.repository.CinemaRepository;
import com.cinema.cinema.repository.HallRepository;
import com.cinema.cinema.repository.SeatRepository;
import com.cinema.shared.config.RedisConfig;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HallService {

    private final HallRepository hallRepository;
    private final CinemaRepository cinemaRepository;
    private final SeatRepository seatRepository;

    @Cacheable(value = RedisConfig.CACHE_HALL_DETAIL, key = "#id")
    public HallResponse getHallById(Long id) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HALL_NOT_FOUND));
        return HallResponse.fromEntity(hall);
    }

    @Cacheable(value = RedisConfig.CACHE_HALL_DETAIL, key = "'withSeats:' + #id")
    public HallResponse getHallByIdWithSeats(Long id) {
        Hall hall = hallRepository.findByIdWithSeats(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HALL_NOT_FOUND));
        return HallResponse.fromEntityWithSeats(hall);
    }

    @Cacheable(value = RedisConfig.CACHE_HALLS, key = "'cinema:' + #cinemaId")
    public List<HallResponse> getHallsByCinemaId(Long cinemaId) {
        return hallRepository.findByCinemaId(cinemaId)
                .stream()
                .map(HallResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = RedisConfig.CACHE_HALLS, key = "'cinema:' + #cinemaId + ':active'")
    public List<HallResponse> getActiveHallsByCinemaId(Long cinemaId) {
        return hallRepository.findActiveHallsByCinemaId(cinemaId)
                .stream()
                .map(HallResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = RedisConfig.CACHE_SEATS, key = "'hall:' + #hallId")
    public List<SeatResponse> getSeatsByHallId(Long hallId) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HALL_NOT_FOUND));
        return seatRepository.findByHallIdOrderBySeat(hallId)
                .stream()
                .map(SeatResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = RedisConfig.CACHE_SEATS, key = "'hall:' + #hallId + ':active'")
    public List<SeatResponse> getActiveSeatsByHallId(Long hallId) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HALL_NOT_FOUND));
        return seatRepository.findActiveSeatsbyHallId(hallId)
                .stream()
                .map(SeatResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisConfig.CACHE_HALLS, key = "'cinema:' + #cinemaId"),
            @CacheEvict(value = RedisConfig.CACHE_HALLS, key = "'cinema:' + #cinemaId + ':active'")
    })
    public HallResponse createHall(Long cinemaId, CreateHallRequest request) {
        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CINEMA_NOT_FOUND));

        if (hallRepository.existsByCinemaIdAndName(cinemaId, request.getName())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
        }

        int totalSeats = request.getTotalRows() * request.getSeatsPerRow();

        Hall hall = Hall.builder()
                .cinema(cinema)
                .name(request.getName())
                .hallType(HallType.valueOf(request.getHallType().toUpperCase()))
                .totalRows(request.getTotalRows())
                .seatsPerRow(request.getSeatsPerRow())
                .totalSeats(totalSeats)
                .status(HallStatus.ACTIVE)
                .seats(new ArrayList<>())
                .build();

        Hall savedHall = hallRepository.save(hall);

        // Auto-generate seats for the hall
        generateSeatsForHall(savedHall, request.getTotalRows(), request.getSeatsPerRow());

        log.info("Created hall: {} in cinema {} (ID: {})", savedHall.getName(), cinema.getName(), savedHall.getId());
        return HallResponse.fromEntity(savedHall);
    }

    private void generateSeatsForHall(Hall hall, int totalRows, int seatsPerRow) {
        List<Seat> seats = new ArrayList<>();

        for (int row = 0; row < totalRows; row++) {
            String rowName = String.valueOf((char) ('A' + row));
            SeatType seatType = determineSeatType(row, totalRows, hall.getHallType());

            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                Seat seat = Seat.builder()
                        .hall(hall)
                        .rowName(rowName)
                        .seatNumber(seatNum)
                        .seatType(seatType)
                        .status(Seat.SeatStatus.ACTIVE)
                        .build();
                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
        log.info("Generated {} seats for hall {}", seats.size(), hall.getName());
    }

    private SeatType determineSeatType(int rowIndex, int totalRows, HallType hallType) {
        // VIP hall: all seats are VIP
        if (hallType == HallType.VIP) {
            return SeatType.VIP;
        }

        // Last 2 rows are VIP (premium seating)
        if (rowIndex >= totalRows - 2) {
            return SeatType.VIP;
        }

        return SeatType.NORMAL;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisConfig.CACHE_HALL_DETAIL, key = "#hallId"),
            @CacheEvict(value = RedisConfig.CACHE_HALL_DETAIL, key = "'withSeats:' + #hallId"),
            @CacheEvict(value = RedisConfig.CACHE_HALLS, allEntries = true)
    })
    public HallResponse updateHall(Long hallId, UpdateHallRequest request) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HALL_NOT_FOUND));

        if (request.getName() != null) {
            if (!request.getName().equals(hall.getName()) &&
                hallRepository.existsByCinemaIdAndName(hall.getCinema().getId(), request.getName())) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
            }
            hall.setName(request.getName());
        }

        if (request.getHallType() != null) {
            hall.setHallType(HallType.valueOf(request.getHallType().toUpperCase()));
        }

        if (request.getStatus() != null) {
            hall.setStatus(HallStatus.valueOf(request.getStatus().toUpperCase()));
        }

        Hall updatedHall = hallRepository.save(hall);
        log.info("Updated hall: {} (ID: {})", updatedHall.getName(), updatedHall.getId());
        return HallResponse.fromEntity(updatedHall);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisConfig.CACHE_HALL_DETAIL, key = "#hallId"),
            @CacheEvict(value = RedisConfig.CACHE_HALL_DETAIL, key = "'withSeats:' + #hallId"),
            @CacheEvict(value = RedisConfig.CACHE_HALLS, allEntries = true),
            @CacheEvict(value = RedisConfig.CACHE_SEATS, key = "'hall:' + #hallId"),
            @CacheEvict(value = RedisConfig.CACHE_SEATS, key = "'hall:' + #hallId + ':active'")
    })
    public void deleteHall(Long hallId) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HALL_NOT_FOUND));

        // Delete all seats first
        seatRepository.deleteByHallId(hallId);

        hallRepository.delete(hall);
        log.info("Deleted hall: {} (ID: {})", hall.getName(), hallId);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisConfig.CACHE_HALL_DETAIL, key = "'withSeats:' + #hallId"),
            @CacheEvict(value = RedisConfig.CACHE_SEATS, key = "'hall:' + #hallId"),
            @CacheEvict(value = RedisConfig.CACHE_SEATS, key = "'hall:' + #hallId + ':active'")
    })
    public void regenerateSeats(Long hallId) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HALL_NOT_FOUND));

        // Delete existing seats
        seatRepository.deleteByHallId(hallId);

        // Regenerate seats
        generateSeatsForHall(hall, hall.getTotalRows(), hall.getSeatsPerRow());

        log.info("Regenerated seats for hall: {} (ID: {})", hall.getName(), hallId);
    }
}
