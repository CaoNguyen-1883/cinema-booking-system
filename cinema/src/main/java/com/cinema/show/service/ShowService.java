package com.cinema.show.service;

import com.cinema.cinema.entity.Hall;
import com.cinema.cinema.entity.Seat;
import com.cinema.cinema.repository.HallRepository;
import com.cinema.cinema.repository.SeatRepository;
import com.cinema.movie.entity.Movie;
import com.cinema.movie.repository.MovieRepository;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.exception.ErrorCode;
import com.cinema.show.dto.*;
import com.cinema.show.entity.Show;
import com.cinema.show.entity.Show.ShowStatus;
import com.cinema.show.entity.ShowSeat;
import com.cinema.show.entity.ShowSeat.ShowSeatStatus;
import com.cinema.show.repository.ShowRepository;
import com.cinema.show.repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShowService {

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    // Buffer time between shows (minutes)
    private static final int SHOW_BUFFER_MINUTES = 20;
    // VIP seat price increase
    private static final BigDecimal VIP_PRICE_INCREASE = new BigDecimal("20000");
    // Weekend price increase
    private static final BigDecimal WEEKEND_PRICE_INCREASE = new BigDecimal("30000");

    public ShowResponse getShowById(Long id) {
        Show show = showRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOW_NOT_FOUND));
        return ShowResponse.fromEntity(show);
    }

    public ShowResponse getShowByIdWithSeats(Long id) {
        Show show = showRepository.findByIdWithSeats(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOW_NOT_FOUND));
        return ShowResponse.fromEntityWithSeats(show);
    }

    public List<ShowSeatResponse> getShowSeats(Long showId) {
        // Verify show exists
        if (!showRepository.existsById(showId)) {
            throw new BusinessException(ErrorCode.SHOW_NOT_FOUND);
        }
        return showSeatRepository.findByShowIdWithSeat(showId).stream()
                .map(ShowSeatResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShowResponse> getShowsByMovie(Long movieId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = startDate.plusDays(7);
        }
        return showRepository.findByMovieIdAndDateRange(movieId, startDate, endDate, ShowStatus.SCHEDULED)
                .stream()
                .map(ShowResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShowResponse> getShowsByCinema(Long cinemaId, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return showRepository.findByCinemaIdAndDate(cinemaId, date, ShowStatus.SCHEDULED)
                .stream()
                .map(ShowResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShowResponse> getUpcomingShows(Long movieId) {
        return showRepository.findUpcomingShowsByMovieId(movieId, LocalDate.now(), LocalTime.now())
                .stream()
                .map(ShowResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<ShowResponse> getAllShows(ShowStatus status, Pageable pageable) {
        if (status != null) {
            return showRepository.findByStatus(status, pageable)
                    .map(ShowResponse::fromEntity);
        }
        return showRepository.findAll(pageable)
                .map(ShowResponse::fromEntity);
    }

    @Transactional
    public ShowResponse createShow(CreateShowRequest request) {
        // Get movie
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        // Get hall with cinema
        Hall hall = hallRepository.findByIdWithSeats(request.getHallId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HALL_NOT_FOUND));

        // Calculate end time
        LocalTime endTime = request.getStartTime()
                .plusMinutes(movie.getDuration())
                .plusMinutes(SHOW_BUFFER_MINUTES);

        // Validate show time is in the future
        if (request.getShowDate().isBefore(LocalDate.now()) ||
            (request.getShowDate().isEqual(LocalDate.now()) && request.getStartTime().isBefore(LocalTime.now()))) {
            throw new BusinessException(ErrorCode.SHOW_TIME_IN_PAST);
        }

        // Check for schedule conflict
        if (showRepository.hasScheduleConflict(
                request.getHallId(),
                request.getShowDate(),
                request.getStartTime(),
                endTime,
                0L)) {
            throw new BusinessException(ErrorCode.SHOW_SCHEDULE_CONFLICT);
        }

        // Create show
        Show show = Show.builder()
                .movie(movie)
                .hall(hall)
                .showDate(request.getShowDate())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .basePrice(request.getBasePrice())
                .status(ShowStatus.SCHEDULED)
                .showSeats(new ArrayList<>())
                .build();

        Show savedShow = showRepository.save(show);

        // Generate show seats
        generateShowSeats(savedShow, hall, request.getBasePrice(), request.getShowDate());

        log.info("Created show: Movie {} at {} on {} at {}",
                movie.getTitle(), hall.getName(), request.getShowDate(), request.getStartTime());

        return ShowResponse.fromEntity(savedShow);
    }

    private void generateShowSeats(Show show, Hall hall, BigDecimal basePrice, LocalDate showDate) {
        List<Seat> activeSeats = seatRepository.findActiveSeatsbyHallId(hall.getId());
        boolean isWeekend = showDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                           showDate.getDayOfWeek() == DayOfWeek.SUNDAY;

        List<ShowSeat> showSeats = new ArrayList<>();
        for (Seat seat : activeSeats) {
            BigDecimal seatPrice = basePrice;

            // Add VIP price increase
            if (seat.getSeatType() == Seat.SeatType.VIP) {
                seatPrice = seatPrice.add(VIP_PRICE_INCREASE);
            } else if (seat.getSeatType() == Seat.SeatType.COUPLE) {
                seatPrice = seatPrice.multiply(new BigDecimal("2"));
            }

            // Add weekend price increase
            if (isWeekend) {
                seatPrice = seatPrice.add(WEEKEND_PRICE_INCREASE);
            }

            ShowSeat showSeat = ShowSeat.builder()
                    .show(show)
                    .seat(seat)
                    .price(seatPrice)
                    .status(ShowSeatStatus.AVAILABLE)
                    .build();
            showSeats.add(showSeat);
        }

        showSeatRepository.saveAll(showSeats);
        log.info("Generated {} show seats for show {}", showSeats.size(), show.getId());
    }

    @Transactional
    public ShowResponse updateShow(Long showId, UpdateShowRequest request) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOW_NOT_FOUND));

        // Check if show can be modified
        if (show.getStatus() == ShowStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.SHOW_CANCELLED);
        }

        // Check if show has started
        if (show.getShowDate().isBefore(LocalDate.now()) ||
            (show.getShowDate().isEqual(LocalDate.now()) && show.getStartTime().isBefore(LocalTime.now()))) {
            throw new BusinessException(ErrorCode.SHOW_ALREADY_STARTED);
        }

        // Check if show has bookings (sold seats)
        boolean hasBookings = showSeatRepository.existsByShowIdAndStatus(showId, ShowSeatStatus.SOLD);

        // Update show date and time
        if (request.getShowDate() != null || request.getStartTime() != null) {
            if (hasBookings) {
                throw new BusinessException(ErrorCode.SHOW_HAS_BOOKINGS);
            }

            LocalDate newDate = request.getShowDate() != null ? request.getShowDate() : show.getShowDate();
            LocalTime newStartTime = request.getStartTime() != null ? request.getStartTime() : show.getStartTime();
            LocalTime newEndTime = newStartTime
                    .plusMinutes(show.getMovie().getDuration())
                    .plusMinutes(SHOW_BUFFER_MINUTES);

            // Validate time is in future
            if (newDate.isBefore(LocalDate.now()) ||
                (newDate.isEqual(LocalDate.now()) && newStartTime.isBefore(LocalTime.now()))) {
                throw new BusinessException(ErrorCode.SHOW_TIME_IN_PAST);
            }

            // Check for schedule conflict
            if (showRepository.hasScheduleConflict(
                    show.getHall().getId(),
                    newDate,
                    newStartTime,
                    newEndTime,
                    showId)) {
                throw new BusinessException(ErrorCode.SHOW_SCHEDULE_CONFLICT);
            }

            show.setShowDate(newDate);
            show.setStartTime(newStartTime);
            show.setEndTime(newEndTime);
        }

        // Update base price
        if (request.getBasePrice() != null) {
            show.setBasePrice(request.getBasePrice());
            // Recalculate all seat prices
            recalculateShowSeatPrices(show, request.getBasePrice());
        }

        // Update status
        if (request.getStatus() != null) {
            ShowStatus newStatus = ShowStatus.valueOf(request.getStatus().toUpperCase());
            show.setStatus(newStatus);
        }

        Show updatedShow = showRepository.save(show);
        log.info("Updated show: {} (ID: {})", updatedShow.getMovie().getTitle(), updatedShow.getId());
        return ShowResponse.fromEntity(updatedShow);
    }

    private void recalculateShowSeatPrices(Show show, BigDecimal newBasePrice) {
        List<ShowSeat> showSeats = showSeatRepository.findByShowIdWithSeat(show.getId());
        boolean isWeekend = show.getShowDate().getDayOfWeek() == DayOfWeek.SATURDAY ||
                           show.getShowDate().getDayOfWeek() == DayOfWeek.SUNDAY;

        for (ShowSeat showSeat : showSeats) {
            // Only update available seats
            if (showSeat.getStatus() != ShowSeatStatus.AVAILABLE) {
                continue;
            }

            BigDecimal seatPrice = newBasePrice;

            if (showSeat.getSeat().getSeatType() == Seat.SeatType.VIP) {
                seatPrice = seatPrice.add(VIP_PRICE_INCREASE);
            } else if (showSeat.getSeat().getSeatType() == Seat.SeatType.COUPLE) {
                seatPrice = seatPrice.multiply(new BigDecimal("2"));
            }

            if (isWeekend) {
                seatPrice = seatPrice.add(WEEKEND_PRICE_INCREASE);
            }

            showSeat.setPrice(seatPrice);
        }

        showSeatRepository.saveAll(showSeats);
    }

    @Transactional
    public void cancelShow(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOW_NOT_FOUND));

        if (show.getStatus() == ShowStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.SHOW_CANCELLED);
        }

        // Update show status
        show.setStatus(ShowStatus.CANCELLED);
        showRepository.save(show);

        // Release all seats
        List<ShowSeat> showSeats = showSeatRepository.findByShowIdWithSeat(showId);
        for (ShowSeat seat : showSeats) {
            seat.unlock();
        }
        showSeatRepository.saveAll(showSeats);

        // TODO: Handle booking cancellations and refunds (in Booking module)

        log.info("Cancelled show: {} (ID: {})", show.getMovie().getTitle(), showId);
    }

    @Transactional
    public void deleteShow(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOW_NOT_FOUND));

        // Check if show has bookings
        boolean hasBookings = showSeatRepository.existsByShowIdAndStatus(showId, ShowSeatStatus.SOLD);
        if (hasBookings) {
            throw new BusinessException(ErrorCode.SHOW_HAS_BOOKINGS);
        }

        // Delete all show seats first
        showSeatRepository.deleteByShowId(showId);

        // Delete show
        showRepository.delete(show);
        log.info("Deleted show: {} (ID: {})", show.getMovie().getTitle(), showId);
    }

    // Get shows by hall and date (for admin schedule view)
    public List<ShowResponse> getShowsByHallAndDate(Long hallId, LocalDate date) {
        return showRepository.findByHallIdAndDate(hallId, date)
                .stream()
                .map(ShowResponse::fromEntityBasic)
                .collect(Collectors.toList());
    }
}
