package com.cinema.show.service;

import com.cinema.cinema.entity.Cinema;
import com.cinema.cinema.entity.Hall;
import com.cinema.cinema.entity.Seat;
import com.cinema.cinema.repository.HallRepository;
import com.cinema.cinema.repository.SeatRepository;
import com.cinema.movie.entity.Movie;
import com.cinema.movie.entity.Movie.MovieRating;
import com.cinema.movie.entity.Movie.MovieStatus;
import com.cinema.movie.repository.MovieRepository;
import com.cinema.shared.exception.BusinessException;
import com.cinema.show.dto.*;
import com.cinema.show.entity.Show;
import com.cinema.show.entity.Show.ShowStatus;
import com.cinema.show.entity.ShowSeat;
import com.cinema.show.entity.ShowSeat.ShowSeatStatus;
import com.cinema.show.repository.ShowRepository;
import com.cinema.show.repository.ShowSeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShowService Unit Tests")
class ShowServiceTest {

    @Mock
    private ShowRepository showRepository;
    @Mock
    private ShowSeatRepository showSeatRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private HallRepository hallRepository;
    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ShowService showService;

    private Show testShow;
    private Movie testMovie;
    private Cinema testCinema;
    private Hall testHall;
    private Seat testSeat;
    private ShowSeat testShowSeat;
    private CreateShowRequest createRequest;

    @BeforeEach
    void setUp() {
        testCinema = Cinema.builder()
                .id(1L)
                .name("Test Cinema")
                .build();

        testHall = Hall.builder()
                .id(1L)
                .name("Hall 1")
                .cinema(testCinema)
                .totalSeats(100)
                .totalRows(10)
                .seatsPerRow(10)
                .seats(new ArrayList<>())
                .build();

        testSeat = Seat.builder()
                .id(1L)
                .rowName("A")
                .seatNumber(1)
                .seatType(Seat.SeatType.NORMAL)
                .status(Seat.SeatStatus.ACTIVE)
                .hall(testHall)
                .build();
        testHall.getSeats().add(testSeat);

        testMovie = Movie.builder()
                .id(1L)
                .title("Test Movie")
                .duration(120)
                .rating(MovieRating.P)
                .status(MovieStatus.NOW_SHOWING)
                .genres(new HashSet<>())
                .build();

        testShow = Show.builder()
                .id(1L)
                .movie(testMovie)
                .hall(testHall)
                .showDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(16, 20))
                .basePrice(new BigDecimal("100000"))
                .status(ShowStatus.SCHEDULED)
                .showSeats(new ArrayList<>())
                .build();

        testShowSeat = ShowSeat.builder()
                .id(1L)
                .show(testShow)
                .seat(testSeat)
                .price(new BigDecimal("100000"))
                .status(ShowSeatStatus.AVAILABLE)
                .build();

        createRequest = CreateShowRequest.builder()
                .movieId(1L)
                .hallId(1L)
                .showDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(14, 0))
                .basePrice(new BigDecimal("100000"))
                .build();
    }

    @Nested
    @DisplayName("Get Show Tests")
    class GetShowTests {

        @Test
        @DisplayName("Should get show by ID successfully")
        void getShowById_Success() {
            // Given
            when(showRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testShow));

            // When
            ShowResponse response = showService.getShowById(1L);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when show not found")
        void getShowById_NotFound_ThrowsException() {
            // Given
            when(showRepository.findByIdWithDetails(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> showService.getShowById(999L))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should get show seats")
        void getShowSeats_Success() {
            // Given
            when(showRepository.existsById(1L)).thenReturn(true);
            when(showSeatRepository.findByShowIdWithSeat(1L)).thenReturn(List.of(testShowSeat));

            // When
            List<ShowSeatResponse> result = showService.getShowSeats(1L);

            // Then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should throw exception when getting seats for non-existent show")
        void getShowSeats_ShowNotFound_ThrowsException() {
            // Given
            when(showRepository.existsById(anyLong())).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> showService.getShowSeats(999L))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should get shows by movie")
        void getShowsByMovie_Success() {
            // Given
            when(showRepository.findByMovieIdAndDateRange(
                    eq(1L), any(LocalDate.class), any(LocalDate.class), eq(ShowStatus.SCHEDULED)))
                    .thenReturn(List.of(testShow));

            // When
            List<ShowResponse> result = showService.getShowsByMovie(1L, null, null);

            // Then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should get shows by cinema")
        void getShowsByCinema_Success() {
            // Given
            when(showRepository.findByCinemaIdAndDate(eq(1L), any(LocalDate.class), eq(ShowStatus.SCHEDULED)))
                    .thenReturn(List.of(testShow));

            // When
            List<ShowResponse> result = showService.getShowsByCinema(1L, null);

            // Then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should get all shows with pagination")
        void getAllShows_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Show> showPage = new PageImpl<>(List.of(testShow));
            when(showRepository.findAll(pageable)).thenReturn(showPage);

            // When
            Page<ShowResponse> result = showService.getAllShows(null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Create Show Tests")
    class CreateShowTests {

        @Test
        @DisplayName("Should create show successfully")
        void createShow_Success() {
            // Given
            when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
            when(hallRepository.findByIdWithSeats(1L)).thenReturn(Optional.of(testHall));
            when(showRepository.hasScheduleConflict(anyLong(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class), anyLong()))
                    .thenReturn(false);
            when(showRepository.save(any(Show.class))).thenAnswer(invocation -> {
                Show show = invocation.getArgument(0);
                show.setId(1L);
                return show;
            });
            when(seatRepository.findActiveSeatsbyHallId(1L)).thenReturn(List.of(testSeat));
            when(showSeatRepository.saveAll(anyList())).thenReturn(List.of(testShowSeat));

            // When
            ShowResponse response = showService.createShow(createRequest);

            // Then
            assertThat(response).isNotNull();
            verify(showRepository).save(any(Show.class));
            verify(showSeatRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Should throw exception when movie not found")
        void createShow_MovieNotFound_ThrowsException() {
            // Given
            when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> showService.createShow(createRequest))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when hall not found")
        void createShow_HallNotFound_ThrowsException() {
            // Given
            when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
            when(hallRepository.findByIdWithSeats(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> showService.createShow(createRequest))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when show time is in past")
        void createShow_TimeInPast_ThrowsException() {
            // Given
            createRequest.setShowDate(LocalDate.now().minusDays(1));
            when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
            when(hallRepository.findByIdWithSeats(1L)).thenReturn(Optional.of(testHall));

            // When & Then
            assertThatThrownBy(() -> showService.createShow(createRequest))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when schedule conflict exists")
        void createShow_ScheduleConflict_ThrowsException() {
            // Given
            when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
            when(hallRepository.findByIdWithSeats(1L)).thenReturn(Optional.of(testHall));
            when(showRepository.hasScheduleConflict(anyLong(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class), anyLong()))
                    .thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> showService.createShow(createRequest))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Update Show Tests")
    class UpdateShowTests {

        @Test
        @DisplayName("Should update show successfully")
        void updateShow_Success() {
            // Given
            UpdateShowRequest updateRequest = UpdateShowRequest.builder()
                    .basePrice(new BigDecimal("120000"))
                    .build();
            when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));
            when(showSeatRepository.existsByShowIdAndStatus(anyLong(), eq(ShowSeatStatus.SOLD))).thenReturn(false);
            when(showRepository.save(any(Show.class))).thenReturn(testShow);
            when(showSeatRepository.findByShowIdWithSeat(anyLong())).thenReturn(List.of(testShowSeat));

            // When
            ShowResponse response = showService.updateShow(1L, updateRequest);

            // Then
            assertThat(response).isNotNull();
            verify(showRepository).save(any(Show.class));
        }

        @Test
        @DisplayName("Should throw exception when show is cancelled")
        void updateShow_Cancelled_ThrowsException() {
            // Given
            testShow.setStatus(ShowStatus.CANCELLED);
            UpdateShowRequest updateRequest = UpdateShowRequest.builder()
                    .basePrice(new BigDecimal("120000"))
                    .build();
            when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));

            // When & Then
            assertThatThrownBy(() -> showService.updateShow(1L, updateRequest))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Cancel Show Tests")
    class CancelShowTests {

        @Test
        @DisplayName("Should cancel show successfully")
        void cancelShow_Success() {
            // Given
            when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));
            when(showRepository.save(any(Show.class))).thenReturn(testShow);
            when(showSeatRepository.findByShowIdWithSeat(1L)).thenReturn(List.of(testShowSeat));
            when(showSeatRepository.saveAll(anyList())).thenReturn(List.of(testShowSeat));

            // When
            showService.cancelShow(1L);

            // Then
            verify(showRepository).save(any(Show.class));
        }

        @Test
        @DisplayName("Should throw exception when show already cancelled")
        void cancelShow_AlreadyCancelled_ThrowsException() {
            // Given
            testShow.setStatus(ShowStatus.CANCELLED);
            when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));

            // When & Then
            assertThatThrownBy(() -> showService.cancelShow(1L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Delete Show Tests")
    class DeleteShowTests {

        @Test
        @DisplayName("Should delete show successfully")
        void deleteShow_Success() {
            // Given
            when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));
            when(showSeatRepository.existsByShowIdAndStatus(anyLong(), eq(ShowSeatStatus.SOLD))).thenReturn(false);
            doNothing().when(showSeatRepository).deleteByShowId(anyLong());
            doNothing().when(showRepository).delete(any(Show.class));

            // When
            showService.deleteShow(1L);

            // Then
            verify(showRepository).delete(testShow);
        }

        @Test
        @DisplayName("Should throw exception when show has bookings")
        void deleteShow_HasBookings_ThrowsException() {
            // Given
            when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));
            when(showSeatRepository.existsByShowIdAndStatus(anyLong(), eq(ShowSeatStatus.SOLD))).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> showService.deleteShow(1L))
                    .isInstanceOf(BusinessException.class);
        }
    }
}
