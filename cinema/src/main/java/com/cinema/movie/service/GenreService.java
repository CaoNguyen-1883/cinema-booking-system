package com.cinema.movie.service;

import com.cinema.movie.dto.CreateGenreRequest;
import com.cinema.movie.dto.GenreResponse;
import com.cinema.movie.entity.Genre;
import com.cinema.movie.repository.GenreRepository;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;

    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAllByOrderByNameAsc()
                .stream()
                .map(GenreResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public GenreResponse getGenreById(Integer id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Genre not found"));
        return GenreResponse.fromEntity(genre);
    }

    public GenreResponse getGenreBySlug(String slug) {
        Genre genre = genreRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Genre not found"));
        return GenreResponse.fromEntity(genre);
    }

    @Transactional
    public GenreResponse createGenre(CreateGenreRequest request) {
        if (genreRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Genre already exists");
        }

        String slug = generateSlug(request.getName());

        Genre genre = Genre.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .build();

        Genre savedGenre = genreRepository.save(genre);
        log.info("Created genre: {} (ID: {})", savedGenre.getName(), savedGenre.getId());
        return GenreResponse.fromEntity(savedGenre);
    }

    @Transactional
    public void deleteGenre(Integer id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Genre not found"));
        genreRepository.delete(genre);
        log.info("Deleted genre: {} (ID: {})", genre.getName(), id);
    }

    private String generateSlug(String name) {
        String slug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();

        // Ensure uniqueness
        String baseSlug = slug;
        int counter = 1;
        while (genreRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }
}
