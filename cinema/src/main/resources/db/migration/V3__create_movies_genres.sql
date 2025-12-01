-- Genres Table
CREATE TABLE genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_genres_name ON genres(name);
CREATE UNIQUE INDEX idx_genres_slug ON genres(slug);

COMMENT ON TABLE genres IS 'Movie genres/categories';

-- Movies Table
CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    original_title VARCHAR(255),
    director VARCHAR(255) NOT NULL,
    cast_members TEXT,
    duration INTEGER NOT NULL,
    release_date DATE NOT NULL,
    end_date DATE,
    rating VARCHAR(10) NOT NULL,
    language VARCHAR(50) NOT NULL DEFAULT 'Vietnamese',
    subtitle VARCHAR(50),
    poster_url VARCHAR(500),
    banner_url VARCHAR(500),
    trailer_url VARCHAR(500),
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'COMING_SOON',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT movies_duration_positive CHECK (duration > 0),
    CONSTRAINT movies_rating_valid CHECK (rating IN ('P', 'K', 'T13', 'T16', 'T18', 'C')),
    CONSTRAINT movies_status_valid CHECK (status IN ('NOW_SHOWING', 'COMING_SOON', 'ENDED'))
);

CREATE INDEX idx_movies_status ON movies(status);
CREATE INDEX idx_movies_release_date ON movies(release_date);

COMMENT ON TABLE movies IS 'Movies catalog';
COMMENT ON COLUMN movies.duration IS 'Duration in minutes';
COMMENT ON COLUMN movies.rating IS 'Age rating: P(All ages), K(Kids), T13(13+), T16(16+), T18(18+), C(Restricted)';

-- Movie_Genres Junction Table
CREATE TABLE movie_genres (
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    genre_id INTEGER NOT NULL REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, genre_id)
);

CREATE INDEX idx_movie_genres_movie_id ON movie_genres(movie_id);
CREATE INDEX idx_movie_genres_genre_id ON movie_genres(genre_id);

COMMENT ON TABLE movie_genres IS 'Many-to-many relationship between movies and genres';

-- Insert default genres
INSERT INTO genres (name, slug, description) VALUES
('Action', 'action', 'Action-packed movies'),
('Adventure', 'adventure', 'Adventure movies'),
('Animation', 'animation', 'Animated movies'),
('Comedy', 'comedy', 'Comedy movies'),
('Crime', 'crime', 'Crime movies'),
('Drama', 'drama', 'Drama movies'),
('Fantasy', 'fantasy', 'Fantasy movies'),
('Horror', 'horror', 'Horror movies'),
('Romance', 'romance', 'Romance movies'),
('Sci-Fi', 'sci-fi', 'Science fiction movies'),
('Thriller', 'thriller', 'Thriller movies'),
('Documentary', 'documentary', 'Documentary films');
