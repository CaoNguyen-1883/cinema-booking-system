package com.cinema.movie.service;

import com.cinema.movie.dto.CreateMovieRequest;
import com.cinema.movie.dto.MovieResponse;
import com.cinema.movie.dto.UpdateMovieRequest;
import com.cinema.movie.entity.Genre;
import com.cinema.movie.entity.Movie;
import com.cinema.movie.entity.Movie.MovieRating;
import com.cinema.movie.entity.Movie.MovieStatus;
import com.cinema.movie.repository.GenreRepository;
import com.cinema.movie.repository.MovieRepository;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovieService Unit Tests")
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie testMovie;
    private Genre testGenre;
    private CreateMovieRequest createRequest;

    @BeforeEach
    void setUp() {
        testGenre = Genre.builder()
                .id(1)
                .name("Action")
                .movies(new HashSet<>())
                .build();

        testMovie = Movie.builder()
                .id(1L)
                .title("Test Movie")
                .originalTitle("Original Title")
                .director("Test Director")
                .castMembers("Actor 1, Actor 2")
                .duration(120)
                .releaseDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(2))
                .rating(MovieRating.P)
                .language("Vietnamese")
                .description("Test description")
                .status(MovieStatus.NOW_SHOWING)
                .genres(new HashSet<>(Set.of(testGenre)))
                .build();

        createRequest = CreateMovieRequest.builder()
                .title("New Movie")
                .originalTitle("New Original Title")
                .director("New Director")
                .castMembers("Actor A, Actor B")
                .duration(150)
                .releaseDate(LocalDate.now().plusDays(30))
                .rating("P")
                .language("English")
                .description("New movie description")
                .status("COMING_SOON")
                .genreIds(Set.of(1))
                .build();
    }

    @Nested
    @DisplayName("Get Movie Tests")
    class GetMovieTests {

        @Test
        @DisplayName("Should get movie by ID successfully")
        void getMovieById_Success() {
            // Given
            when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));

            // When
            MovieResponse response = movieService.getMovieById(1L);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("Test Movie");
            verify(movieRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when movie not found")
        void getMovieById_NotFound_ThrowsException() {
            // Given
            when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> movieService.getMovieById(999L))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should get all movies with pagination")
        void getAllMovies_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Movie> moviePage = new PageImpl<>(List.of(testMovie));
            when(movieRepository.findAll(pageable)).thenReturn(moviePage);

            // When
            Page<MovieResponse> result = movieService.getAllMovies(pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Movie");
        }

        @Test
        @DisplayName("Should get now showing movies")
        void getNowShowingMovies_Success() {
            // Given
            when(movieRepository.findByStatusOrderByReleaseDateDesc(MovieStatus.NOW_SHOWING))
                    .thenReturn(List.of(testMovie));

            // When
            List<MovieResponse> result = movieService.getNowShowingMovies();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo("NOW_SHOWING");
        }

        @Test
        @DisplayName("Should get coming soon movies")
        void getComingSoonMovies_Success() {
            // Given
            testMovie.setStatus(MovieStatus.COMING_SOON);
            when(movieRepository.findByStatusOrderByReleaseDateDesc(MovieStatus.COMING_SOON))
                    .thenReturn(List.of(testMovie));

            // When
            List<MovieResponse> result = movieService.getComingSoonMovies();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo("COMING_SOON");
        }
    }

    @Nested
    @DisplayName("Create Movie Tests")
    class CreateMovieTests {

        @Test
        @DisplayName("Should create movie successfully")
        void createMovie_Success() {
            // Given
            when(genreRepository.findAllById(any())).thenReturn(List.of(testGenre));
            when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
                Movie movie = invocation.getArgument(0);
                movie.setId(1L);
                return movie;
            });

            // When
            MovieResponse response = movieService.createMovie(createRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("New Movie");
            verify(movieRepository).save(any(Movie.class));
        }

        @Test
        @DisplayName("Should create movie without genres")
        void createMovie_WithoutGenres_Success() {
            // Given
            createRequest.setGenreIds(null);
            when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
                Movie movie = invocation.getArgument(0);
                movie.setId(1L);
                return movie;
            });

            // When
            MovieResponse response = movieService.createMovie(createRequest);

            // Then
            assertThat(response).isNotNull();
            verify(genreRepository, never()).findAllById(any());
        }
    }

    @Nested
    @DisplayName("Update Movie Tests")
    class UpdateMovieTests {

        @Test
        @DisplayName("Should update movie successfully")
        void updateMovie_Success() {
            // Given
            UpdateMovieRequest updateRequest = UpdateMovieRequest.builder()
                    .title("Updated Title")
                    .build();
            when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
            when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

            // When
            MovieResponse response = movieService.updateMovie(1L, updateRequest);

            // Then
            assertThat(response).isNotNull();
            verify(movieRepository).save(any(Movie.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent movie")
        void updateMovie_NotFound_ThrowsException() {
            // Given
            UpdateMovieRequest updateRequest = UpdateMovieRequest.builder()
                    .title("Updated Title")
                    .build();
            when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> movieService.updateMovie(999L, updateRequest))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Delete Movie Tests")
    class DeleteMovieTests {

        @Test
        @DisplayName("Should delete movie successfully")
        void deleteMovie_Success() {
            // Given
            when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
            doNothing().when(movieRepository).delete(any(Movie.class));

            // When
            movieService.deleteMovie(1L);

            // Then
            verify(movieRepository).delete(testMovie);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent movie")
        void deleteMovie_NotFound_ThrowsException() {
            // Given
            when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> movieService.deleteMovie(999L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Search Movie Tests")
    class SearchMovieTests {

        @Test
        @DisplayName("Should search movies by keyword")
        void searchMovies_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Movie> moviePage = new PageImpl<>(List.of(testMovie));
            when(movieRepository.searchByKeyword("Test", pageable)).thenReturn(moviePage);

            // When
            Page<MovieResponse> result = movieService.searchMovies("Test", pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should get movies by genre")
        void getMoviesByGenre_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Movie> moviePage = new PageImpl<>(List.of(testMovie));
            when(movieRepository.findByGenreId(1, pageable)).thenReturn(moviePage);

            // When
            Page<MovieResponse> result = movieService.getMoviesByGenre(1, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should get movies by status")
        void getMoviesByStatus_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Movie> moviePage = new PageImpl<>(List.of(testMovie));
            when(movieRepository.findByStatus(MovieStatus.NOW_SHOWING, pageable)).thenReturn(moviePage);

            // When
            Page<MovieResponse> result = movieService.getMoviesByStatus("NOW_SHOWING", pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }
    }
}
