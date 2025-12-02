# Movies Table
s/movie_id BIGSERIAL PRIMARY KEY,/id BIGSERIAL PRIMARY KEY,/g
s/cast TEXT,/cast_members TEXT,/g
s/cast TEXT,\n    genre VARCHAR(255) NOT NULL,//g
s/release_date DATE NOT NULL,/release_date DATE NOT NULL,\n    end_date DATE,/g
s/poster_url VARCHAR(500),/poster_url VARCHAR(500),\n    banner_url VARCHAR(500),/g
s/language VARCHAR(50) NOT NULL DEFAULT 'English'/language VARCHAR(50) NOT NULL DEFAULT 'Vietnamese'/g

# Cinemas Table
s/cinema_id BIGSERIAL PRIMARY KEY,/id BIGSERIAL PRIMARY KEY,/g
s/opening_hours VARCHAR(100) DEFAULT '06:00-24:00'/opening_hours VARCHAR(100) DEFAULT '08:00-24:00'/g

# Halls Table
s/hall_id BIGSERIAL PRIMARY KEY,/id BIGSERIAL PRIMARY KEY,/g
s/hall_type IN ('STANDARD', 'VIP', 'IMAX', '3D', '4DX')/hall_type IN ('STANDARD', 'VIP', 'IMAX', 'THREE_D', 'FOUR_DX')/g

# Seats Table
s/seat_id BIGSERIAL PRIMARY KEY,/id BIGSERIAL PRIMARY KEY,/g
s/row_number VARCHAR(5) NOT NULL,/row_name VARCHAR(5) NOT NULL,/g
s/seat_type IN ('NORMAL', 'VIP', 'PREMIUM', 'COUPLE')/seat_type IN ('NORMAL', 'VIP', 'COUPLE')/g
s/(hall_id, row_number, seat_number)/(hall_id, row_name, seat_number)/g

# Shows Table
s/show_id BIGSERIAL PRIMARY KEY,/id BIGSERIAL PRIMARY KEY,/g
s/show_time TIME NOT NULL,/start_time TIME NOT NULL,\n    end_time TIME NOT NULL,/g

# Show_Seats Table
s/show_seat_id BIGSERIAL PRIMARY KEY,/id BIGSERIAL PRIMARY KEY,/g

# Bookings Table
s/booking_id BIGSERIAL PRIMARY KEY,/id BIGSERIAL PRIMARY KEY,\n    booking_code VARCHAR(20) NOT NULL UNIQUE,/g
s/qr_code TEXT,/qr_code TEXT,\n    expires_at TIMESTAMP,/g
s/paid_at TIMESTAMP,/confirmed_at TIMESTAMP,/g
s/status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'FAILED', 'REFUNDED')/status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED')/g

# Booking_Seats Table
s/booking_seat_id BIGSERIAL PRIMARY KEY,/id BIGSERIAL PRIMARY KEY,/g

# Genres Table
s/genre_id SERIAL PRIMARY KEY,/id SERIAL PRIMARY KEY,/g

# Payment Table
s/payment_id BIGSERIAL PRIMARY KEY,/id BIGSERIAL PRIMARY KEY,/g
s/payment_method IN ('VNPAY', 'MOMO', 'ZALOPAY', 'CARD', 'CASH', 'BANK_TRANSFER')/payment_method IN ('VNPAY', 'MOMO', 'ZALOPAY', 'CASH')/g
s/status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED', 'EXPIRED')/status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED')/g
s/callback_data JSONB,/callback_data TEXT,/g
