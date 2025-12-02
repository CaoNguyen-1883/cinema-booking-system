package com.cinema.movie.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGenreRequest {
    @NotBlank(message = "Genre name is required")
    @Size(max = 50)
    private String name;

    private String description;
}
