package com.cinema.movie.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String originalTitle;

    @NotBlank(message = "Director is required")
    @Size(max = 255)
    private String director;

    private String castMembers;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 600, message = "Duration cannot exceed 600 minutes")
    private Integer duration;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    private LocalDate endDate;

    @NotNull(message = "Rating is required")
    private String rating; // P, K, T13, T16, T18, C

    private String language = "Vietnamese";
    private String subtitle;
    private String posterUrl;
    private String bannerUrl;
    private String trailerUrl;
    private String description;
    private String status = "COMING_SOON"; // NOW_SHOWING, COMING_SOON, ENDED
    private Set<Integer> genreIds;
}
