-- Shows Table
CREATE TABLE shows (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE RESTRICT,
    hall_id BIGINT NOT NULL REFERENCES halls(id) ON DELETE CASCADE,
    show_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT shows_base_price_positive CHECK (base_price > 0),
    CONSTRAINT shows_status_valid CHECK (status IN ('SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX idx_shows_movie_id ON shows(movie_id);
CREATE INDEX idx_shows_hall_id ON shows(hall_id);
CREATE INDEX idx_shows_date ON shows(show_date);
CREATE INDEX idx_shows_status ON shows(status);
CREATE INDEX idx_shows_movie_date ON shows(movie_id, show_date);

COMMENT ON TABLE shows IS 'Movie show schedules';
COMMENT ON COLUMN shows.base_price IS 'Base ticket price in VND';

-- Show_Seats Table
CREATE TABLE show_seats (
    id BIGSERIAL PRIMARY KEY,
    show_id BIGINT NOT NULL REFERENCES shows(id) ON DELETE CASCADE,
    seat_id BIGINT NOT NULL REFERENCES seats(id) ON DELETE CASCADE,
    price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    locked_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    locked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT show_seats_price_positive CHECK (price > 0),
    CONSTRAINT show_seats_status_valid CHECK (status IN ('AVAILABLE', 'LOCKED', 'SOLD')),
    UNIQUE (show_id, seat_id)
);

CREATE INDEX idx_show_seats_show_id ON show_seats(show_id);
CREATE INDEX idx_show_seats_seat_id ON show_seats(seat_id);
CREATE INDEX idx_show_seats_status ON show_seats(status);
CREATE INDEX idx_show_seats_locked_by ON show_seats(locked_by);
CREATE INDEX idx_show_seats_availability ON show_seats(show_id, status);

COMMENT ON TABLE show_seats IS 'Seat availability for each show';
COMMENT ON COLUMN show_seats.status IS 'AVAILABLE, LOCKED, SOLD';
COMMENT ON COLUMN show_seats.locked_by IS 'User who locked this seat';
