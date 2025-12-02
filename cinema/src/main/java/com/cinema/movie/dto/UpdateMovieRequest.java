package com.cinema.movie.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMovieRequest {
    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String originalTitle;

    @Size(max = 255)
    private String director;

    private String castMembers;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 600, message = "Duration cannot exceed 600 minutes")
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
    private Set<Integer> genreIds;
}
