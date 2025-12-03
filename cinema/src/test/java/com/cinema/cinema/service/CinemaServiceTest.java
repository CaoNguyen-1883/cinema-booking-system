package com.cinema.cinema.service;

import com.cinema.cinema.dto.*;
import com.cinema.cinema.entity.Cinema;
import com.cinema.cinema.entity.Cinema.CinemaStatus;
import com.cinema.cinema.repository.CinemaRepository;
import com.cinema.shared.exception.BusinessException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CinemaService Unit Tests")
class CinemaServiceTest {

    @Mock
    private CinemaRepository cinemaRepository;

    @InjectMocks
    private CinemaService cinemaService;

    private Cinema testCinema;
    private CreateCinemaRequest createRequest;

    @BeforeEach
    void setUp() {
        testCinema = Cinema.builder()
                .id(1L)
                .name("Test Cinema")
                .address("123 Main St")
                .city("Ho Chi Minh")
                .district("District 1")
                .phoneNumber("0123456789")
                .email("test@cinema.com")
                .openingHours("08:00-24:00")
                .facilities("Parking, Food Court")
                .status(CinemaStatus.ACTIVE)
                .halls(new ArrayList<>())
                .build();

        createRequest = CreateCinemaRequest.builder()
                .name("New Cinema")
                .address("456 New St")
                .city("Ha Noi")
                .district("Hoan Kiem")
                .phoneNumber("0987654321")
                .email("new@cinema.com")
                .openingHours("09:00-23:00")
                .facilities("Parking")
                .build();
    }

    @Nested
    @DisplayName("Get Cinema Tests")
    class GetCinemaTests {

        @Test
        @DisplayName("Should get cinema by ID successfully")
        void getCinemaById_Success() {
            // Given
            when(cinemaRepository.findById(1L)).thenReturn(Optional.of(testCinema));

            // When
            CinemaResponse response = cinemaService.getCinemaById(1L);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Test Cinema");
        }

        @Test
        @DisplayName("Should throw exception when cinema not found")
        void getCinemaById_NotFound_ThrowsException() {
            // Given
            when(cinemaRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> cinemaService.getCinemaById(999L))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should get cinema with halls")
        void getCinemaByIdWithHalls_Success() {
            // Given
            when(cinemaRepository.findByIdWithHalls(1L)).thenReturn(Optional.of(testCinema));

            // When
            CinemaResponse response = cinemaService.getCinemaByIdWithHalls(1L);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should get all cinemas with pagination")
        void getAllCinemas_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Cinema> cinemaPage = new PageImpl<>(List.of(testCinema));
            when(cinemaRepository.findAll(pageable)).thenReturn(cinemaPage);

            // When
            Page<CinemaResponse> result = cinemaService.getAllCinemas(pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should get active cinemas")
        void getActiveCinemas_Success() {
            // Given
            when(cinemaRepository.findByStatusOrderByNameAsc(CinemaStatus.ACTIVE))
                    .thenReturn(List.of(testCinema));

            // When
            List<CinemaResponse> result = cinemaService.getActiveCinemas();

            // Then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should get cinemas by city")
        void getCinemasByCity_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Cinema> cinemaPage = new PageImpl<>(List.of(testCinema));
            when(cinemaRepository.findByCity("Ho Chi Minh", pageable)).thenReturn(cinemaPage);

            // When
            Page<CinemaResponse> result = cinemaService.getCinemasByCity("Ho Chi Minh", pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should get distinct cities")
        void getDistinctCities_Success() {
            // Given
            when(cinemaRepository.findDistinctCities()).thenReturn(List.of("Ho Chi Minh", "Ha Noi"));

            // When
            List<String> result = cinemaService.getDistinctCities();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).contains("Ho Chi Minh", "Ha Noi");
        }
    }

    @Nested
    @DisplayName("Create Cinema Tests")
    class CreateCinemaTests {

        @Test
        @DisplayName("Should create cinema successfully")
        void createCinema_Success() {
            // Given
            when(cinemaRepository.existsByNameAndCity(anyString(), anyString())).thenReturn(false);
            when(cinemaRepository.save(any(Cinema.class))).thenAnswer(invocation -> {
                Cinema cinema = invocation.getArgument(0);
                cinema.setId(1L);
                return cinema;
            });

            // When
            CinemaResponse response = cinemaService.createCinema(createRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("New Cinema");
            verify(cinemaRepository).save(any(Cinema.class));
        }

        @Test
        @DisplayName("Should throw exception when cinema name exists in city")
        void createCinema_DuplicateName_ThrowsException() {
            // Given
            when(cinemaRepository.existsByNameAndCity("New Cinema", "Ha Noi")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> cinemaService.createCinema(createRequest))
                    .isInstanceOf(BusinessException.class);
            verify(cinemaRepository, never()).save(any(Cinema.class));
        }
    }

    @Nested
    @DisplayName("Update Cinema Tests")
    class UpdateCinemaTests {

        @Test
        @DisplayName("Should update cinema successfully")
        void updateCinema_Success() {
            // Given
            UpdateCinemaRequest updateRequest = UpdateCinemaRequest.builder()
                    .name("Updated Cinema")
                    .address("789 Updated St")
                    .build();
            when(cinemaRepository.findById(1L)).thenReturn(Optional.of(testCinema));
            when(cinemaRepository.save(any(Cinema.class))).thenReturn(testCinema);

            // When
            CinemaResponse response = cinemaService.updateCinema(1L, updateRequest);

            // Then
            assertThat(response).isNotNull();
            verify(cinemaRepository).save(any(Cinema.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent cinema")
        void updateCinema_NotFound_ThrowsException() {
            // Given
            UpdateCinemaRequest updateRequest = UpdateCinemaRequest.builder()
                    .name("Updated Cinema")
                    .build();
            when(cinemaRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> cinemaService.updateCinema(999L, updateRequest))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Delete Cinema Tests")
    class DeleteCinemaTests {

        @Test
        @DisplayName("Should delete cinema successfully")
        void deleteCinema_Success() {
            // Given
            when(cinemaRepository.findById(1L)).thenReturn(Optional.of(testCinema));
            doNothing().when(cinemaRepository).delete(any(Cinema.class));

            // When
            cinemaService.deleteCinema(1L);

            // Then
            verify(cinemaRepository).delete(testCinema);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent cinema")
        void deleteCinema_NotFound_ThrowsException() {
            // Given
            when(cinemaRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> cinemaService.deleteCinema(999L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Search Cinema Tests")
    class SearchCinemaTests {

        @Test
        @DisplayName("Should search cinemas by keyword")
        void searchCinemas_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Cinema> cinemaPage = new PageImpl<>(List.of(testCinema));
            when(cinemaRepository.searchByKeyword("Test", pageable)).thenReturn(cinemaPage);

            // When
            Page<CinemaResponse> result = cinemaService.searchCinemas("Test", pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should get cinemas by status")
        void getCinemasByStatus_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Cinema> cinemaPage = new PageImpl<>(List.of(testCinema));
            when(cinemaRepository.findByStatus(CinemaStatus.ACTIVE, pageable)).thenReturn(cinemaPage);

            // When
            Page<CinemaResponse> result = cinemaService.getCinemasByStatus("ACTIVE", pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }
    }
}
