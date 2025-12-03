package com.cinema.shared.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    // Cache names
    public static final String CACHE_MOVIES = "movies";
    public static final String CACHE_MOVIE_DETAIL = "movie_detail";
    public static final String CACHE_CINEMAS = "cinemas";
    public static final String CACHE_CINEMA_DETAIL = "cinema_detail";
    public static final String CACHE_GENRES = "genres";
    public static final String CACHE_SHOWS = "shows";
    public static final String CACHE_SHOW_SEATS = "show_seats";

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default cache configuration - 10 minutes TTL
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // Custom TTL for specific caches
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Movies cache - 30 minutes (movies don't change often)
        cacheConfigurations.put(CACHE_MOVIES, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put(CACHE_MOVIE_DETAIL, defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // Cinemas cache - 1 hour (cinemas rarely change)
        cacheConfigurations.put(CACHE_CINEMAS, defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put(CACHE_CINEMA_DETAIL, defaultConfig.entryTtl(Duration.ofHours(1)));

        // Genres cache - 1 hour (genres rarely change)
        cacheConfigurations.put(CACHE_GENRES, defaultConfig.entryTtl(Duration.ofHours(1)));

        // Shows cache - 5 minutes (shows change more frequently)
        cacheConfigurations.put(CACHE_SHOWS, defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // Show seats cache - 30 seconds (seats availability changes frequently)
        cacheConfigurations.put(CACHE_SHOW_SEATS, defaultConfig.entryTtl(Duration.ofSeconds(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
