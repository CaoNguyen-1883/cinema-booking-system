-- Cinemas Table
CREATE TABLE cinemas (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100),
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    opening_hours VARCHAR(100) DEFAULT '08:00-24:00',
    facilities TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT cinemas_status_valid CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE'))
);

CREATE INDEX idx_cinemas_city ON cinemas(city);
CREATE INDEX idx_cinemas_status ON cinemas(status);

COMMENT ON TABLE cinemas IS 'Cinema locations';

-- Halls Table
CREATE TABLE halls (
    id BIGSERIAL PRIMARY KEY,
    cinema_id BIGINT NOT NULL REFERENCES cinemas(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    hall_type VARCHAR(20) NOT NULL DEFAULT 'STANDARD',
    total_rows INTEGER NOT NULL,
    seats_per_row INTEGER NOT NULL,
    total_seats INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT halls_total_seats_positive CHECK (total_seats > 0),
    CONSTRAINT halls_hall_type_valid CHECK (hall_type IN ('STANDARD', 'VIP', 'IMAX', '3D', '4DX')),
    CONSTRAINT halls_status_valid CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE')),
    UNIQUE (cinema_id, name)
);

CREATE INDEX idx_halls_cinema_id ON halls(cinema_id);
CREATE INDEX idx_halls_status ON halls(status);

COMMENT ON TABLE halls IS 'Cinema halls/screens';
COMMENT ON COLUMN halls.hall_type IS 'STANDARD, VIP, IMAX, 3D, 4DX';

-- Seats Table
CREATE TABLE seats (
    id BIGSERIAL PRIMARY KEY,
    hall_id BIGINT NOT NULL REFERENCES halls(id) ON DELETE CASCADE,
    row_name VARCHAR(5) NOT NULL,
    seat_number INTEGER NOT NULL,
    seat_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT seats_seat_type_valid CHECK (seat_type IN ('NORMAL', 'VIP', 'COUPLE')),
    CONSTRAINT seats_status_valid CHECK (status IN ('ACTIVE', 'INACTIVE', 'BROKEN')),
    UNIQUE (hall_id, row_name, seat_number)
);

CREATE INDEX idx_seats_hall_id ON seats(hall_id);
CREATE INDEX idx_seats_status ON seats(status);

COMMENT ON TABLE seats IS 'Physical seats in halls';
COMMENT ON COLUMN seats.seat_type IS 'NORMAL, VIP, COUPLE';
