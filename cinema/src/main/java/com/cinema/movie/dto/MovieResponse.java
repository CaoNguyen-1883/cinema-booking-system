package com.cinema.movie.dto;

import com.cinema.movie.entity.Movie;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {
    private Long id;
    private String title;
    private String originalTitle;
    private String director;
    private String castMembers;
    private Integer duration;
    private LocalDate releaseDate;
    private LocalDate endDate;
    private String rating;
    private String language;
    private String subtitle;
    private String posterUrl;
    private String bannerUrl;
    private String trailerUrl;
    private String description;
    private String status;
    private Set<GenreResponse> genres;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MovieResponse fromEntity(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .director(movie.getDirector())
                .castMembers(movie.getCastMembers())
                .duration(movie.getDuration())
                .releaseDate(movie.getReleaseDate())
                .endDate(movie.getEndDate())
                .rating(movie.getRating().name())
                .language(movie.getLanguage())
                .subtitle(movie.getSubtitle())
                .posterUrl(movie.getPosterUrl())
                .bannerUrl(movie.getBannerUrl())
                .trailerUrl(movie.getTrailerUrl())
                .description(movie.getDescription())
                .status(movie.getStatus().name())
                .genres(movie.getGenres().stream()
                        .map(GenreResponse::fromEntity)
                        .collect(Collectors.toSet()))
                .createdAt(movie.getCreatedAt())
                .updatedAt(movie.getUpdatedAt())
                .build();
    }
}
