package com.cinema.movie.service;

import com.cinema.movie.dto.CreateGenreRequest;
import com.cinema.movie.dto.GenreResponse;
import com.cinema.movie.entity.Genre;
import com.cinema.movie.repository.GenreRepository;
import com.cinema.shared.config.RedisConfig;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = RedisConfig.CACHE_GENRES, key = "'all'")
    public List<GenreResponse> getAllGenres() {
        log.debug("Fetching all genres from database");
        return genreRepository.findAllByOrderByNameAsc()
                .stream()
                .map(GenreResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = RedisConfig.CACHE_GENRES, key = "#id")
    public GenreResponse getGenreById(Integer id) {
        log.debug("Fetching genre {} from database", id);
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Genre not found"));
        return GenreResponse.fromEntity(genre);
    }

    @Cacheable(value = RedisConfig.CACHE_GENRES, key = "'slug:' + #slug")
    public GenreResponse getGenreBySlug(String slug) {
        log.debug("Fetching genre by slug {} from database", slug);
        Genre genre = genreRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Genre not found"));
        return GenreResponse.fromEntity(genre);
    }

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_GENRES, allEntries = true)
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
    @CacheEvict(value = RedisConfig.CACHE_GENRES, allEntries = true)
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
