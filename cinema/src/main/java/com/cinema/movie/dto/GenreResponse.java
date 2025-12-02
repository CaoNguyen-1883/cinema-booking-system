package com.cinema.movie.dto;

import com.cinema.movie.entity.Genre;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreResponse {
    private Integer id;
    private String name;
    private String slug;
    private String description;
    private LocalDateTime createdAt;

    public static GenreResponse fromEntity(Genre genre) {
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .slug(genre.getSlug())
                .description(genre.getDescription())
                .createdAt(genre.getCreatedAt())
                .build();
    }
}
