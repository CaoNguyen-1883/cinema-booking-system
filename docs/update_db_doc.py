#!/usr/bin/env python3
"""
Script to update the database design document to match actual code implementation
"""

import re

def update_document(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. Users Table - change user_id to id, add fields, remove email constraint
    content = re.sub(
        r'CREATE TABLE users \(\s+user_id BIGSERIAL PRIMARY KEY,',
        'CREATE TABLE users (\n    id BIGSERIAL PRIMARY KEY,',
        content
    )

    content = re.sub(
        r'(    phone_number VARCHAR\(20\),)\s+(    points INTEGER NOT NULL DEFAULT 0,)',
        r'\1\n    avatar_url VARCHAR(500),\n    \2',
        content
    )

    content = re.sub(
        r'(    points INTEGER NOT NULL DEFAULT 0,)\s+(    role VARCHAR\(20\) NOT NULL DEFAULT \'CUSTOMER\',)',
        r'\1\n    token_version BIGINT NOT NULL DEFAULT 0,\n    \2',
        content
    )

    content = re.sub(
        r'    CONSTRAINT users_email_format CHECK \(email ~\* \'\^[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\\.[A-Z\|a-z]{2,}\$\'\),\n',
        '',
        content
    )

    content = re.sub(
        r'COMMENT ON COLUMN users\.password_hash IS \'BCrypt hashed password\';',
        "COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';\nCOMMENT ON COLUMN users.avatar_url IS 'URL to user profile avatar image';",
        content
    )

    content = re.sub(
        r'COMMENT ON COLUMN users\.points IS \'Loyalty points \(1000 VND = 1 point\)\';',
        "COMMENT ON COLUMN users.points IS 'Loyalty points (1000 VND = 1 point)';\nCOMMENT ON COLUMN users.token_version IS 'Version number for JWT token invalidation';",
        content
    )

    # 2. Movies Table - change movie_id to id, add banner_url, end_date, cast_members
    content = re.sub(
        r'CREATE TABLE movies \(\s+movie_id BIGSERIAL PRIMARY KEY,',
        'CREATE TABLE movies (\n    id BIGSERIAL PRIMARY KEY,',
        content
    )

    content = re.sub(
        r'(    director VARCHAR\(255\) NOT NULL,)\s+(    cast TEXT,)',
        r'\1\n    cast_members TEXT,',
        content
    )

    content = content.replace(
        '    cast TEXT,\n    genre VARCHAR(255) NOT NULL,',
        ''
    )

    content = re.sub(
        r'(    duration INTEGER NOT NULL,)\s+(    release_date DATE NOT NULL,)',
        r'\1\n    \2\n    end_date DATE,',
        content
    )

    content = re.sub(
        r'(    poster_url VARCHAR\(500\),)',
        r'\1\n    banner_url VARCHAR(500),',
        content
    )

    content = re.sub(
        r"language VARCHAR\(50\) NOT NULL DEFAULT 'English'",
        "language VARCHAR(50) NOT NULL DEFAULT 'Vietnamese'",
        content
    )

    # Add note about genres relationship
    content = re.sub(
        r'-- Comments\nCOMMENT ON TABLE movies IS \'Movies catalog\';',
        "-- Comments\nCOMMENT ON TABLE movies IS 'Movies catalog';\nCOMMENT ON COLUMN movies.cast_members IS 'Comma-separated list of cast members';\nCOMMENT ON COLUMN movies.banner_url IS 'URL to movie banner image for headers';",
        content
    )

    # 3. Cinemas Table - change cinema_id to id, update opening_hours
    content = re.sub(
        r'CREATE TABLE cinemas \(\s+cinema_id BIGSERIAL PRIMARY KEY,',
        'CREATE TABLE cinemas (\n    id BIGSERIAL PRIMARY KEY,',
        content
    )

    content = re.sub(
        r"opening_hours VARCHAR\(100\) DEFAULT '06:00-24:00'",
        "opening_hours VARCHAR(100) DEFAULT '08:00-24:00'",
        content
    )

    # 4. Halls Table - change hall_id to id, replace seat_layout
    content = re.sub(
        r'CREATE TABLE halls \(\s+hall_id BIGSERIAL PRIMARY KEY,',
        'CREATE TABLE halls (\n    id BIGSERIAL PRIMARY KEY,',
        content
    )

    content = re.sub(
        r'(    hall_type VARCHAR\(20\) NOT NULL DEFAULT \'STANDARD\',)\s+(    total_seats INTEGER NOT NULL,)\s+(    seat_layout JSON,)\s+(    screen_type VARCHAR\(50\),)\s+(    sound_system VARCHAR\(50\),)',
        r'\1\n    total_rows INTEGER NOT NULL,\n    seats_per_row INTEGER NOT NULL,\n    \2',
        content
    )

    content = re.sub(
        r"hall_type IN \('STANDARD', 'VIP', 'IMAX', '3D', '4DX'\)",
        "hall_type IN ('STANDARD', 'VIP', 'IMAX', 'THREE_D', 'FOUR_DX')",
        content
    )

    content = re.sub(
        r"COMMENT ON COLUMN halls\.seat_layout IS 'JSON structure defining rows, columns, aisles';\nCOMMENT ON COLUMN halls\.hall_type IS 'STANDARD, VIP, IMAX, 3D, 4DX';",
        "COMMENT ON COLUMN halls.total_rows IS 'Number of seat rows in the hall';\nCOMMENT ON COLUMN halls.seats_per_row IS 'Number of seats per row';\nCOMMENT ON COLUMN halls.hall_type IS 'STANDARD, VIP, IMAX, THREE_D, FOUR_DX';",
        content
    )

    # 5. Seats Table - change seat_id to id, row_number to row_name, remove position columns
    content = re.sub(
        r'CREATE TABLE seats \(\s+seat_id BIGSERIAL PRIMARY KEY,',
        'CREATE TABLE seats (\n    id BIGSERIAL PRIMARY KEY,',
        content
    )

    content = re.sub(
        r'    row_number VARCHAR\(5\) NOT NULL,',
        '    row_name VARCHAR(5) NOT NULL,',
        content
    )

    content = re.sub(
        r'(    seat_type VARCHAR\(20\) NOT NULL DEFAULT \'NORMAL\',)\s+(    position_x INTEGER,)\s+(    position_y INTEGER,)',
        r'\1',
        content
    )

    content = re.sub(
        r"seat_type IN \('NORMAL', 'VIP', 'PREMIUM', 'COUPLE'\)",
        "seat_type IN ('NORMAL', 'VIP', 'COUPLE')",
        content
    )

    content = re.sub(
        r'UNIQUE \(hall_id, row_number, seat_number\)',
        'UNIQUE (hall_id, row_name, seat_number)',
        content
    )

    content = re.sub(
        r'CREATE UNIQUE INDEX idx_seats_unique_position ON seats\(hall_id, row_number, seat_number\);',
        'CREATE UNIQUE INDEX idx_seats_unique_position ON seats(hall_id, row_name, seat_number);',
        content
    )

    content = re.sub(
        r"COMMENT ON COLUMN seats\.seat_type IS 'NORMAL, VIP, PREMIUM, COUPLE';\nCOMMENT ON COLUMN seats\.position_x IS 'X coordinate for seat map visualization';\nCOMMENT ON COLUMN seats\.position_y IS 'Y coordinate for seat map visualization';",
        "COMMENT ON COLUMN seats.row_name IS 'Row identifier (A, B, C, etc.)';\nCOMMENT ON COLUMN seats.seat_type IS 'NORMAL, VIP, COUPLE';",
        content
    )

    # 6. Shows Table - change show_id to id, show_time to start_time, add end_time
    content = re.sub(
        r'CREATE TABLE shows \(\s+show_id BIGSERIAL PRIMARY KEY,',
        'CREATE TABLE shows (\n    id BIGSERIAL PRIMARY KEY,',
        content
    )

    content = re.sub(
        r'(    show_date DATE NOT NULL,)\s+(    show_time TIME NOT NULL,)',
        r'\1\n    start_time TIME NOT NULL,\n    end_time TIME NOT NULL,',
        content
    )

    content = re.sub(
        r"COMMENT ON COLUMN shows\.status IS 'SCHEDULED, ONGOING, COMPLETED, CANCELLED';",
        "COMMENT ON COLUMN shows.start_time IS 'Show start time';\nCOMMENT ON COLUMN shows.end_time IS 'Show end time (calculated from start_time + movie duration)';\nCOMMENT ON COLUMN shows.status IS 'SCHEDULED, ONGOING, COMPLETED, CANCELLED';",
        content
    )

    # 7. Show_Seats Table - change show_seat_id to id
    content = re.sub(
        r'CREATE TABLE show_seats \(\s+show_seat_id BIGSERIAL PRIMARY KEY,',
        'CREATE TABLE show_seats (\n    id BIGSERIAL PRIMARY KEY,',
        content
    )

    # 8. Bookings Table - major changes
    content = re.sub(
        r'CREATE TABLE bookings \(\s+booking_id BIGSERIAL PRIMARY KEY,',
        'CREATE TABLE bookings (\n    id BIGSERIAL PRIMARY KEY,\n    booking_code VARCHAR(20) NOT NULL UNIQUE,',
        content
    )

    content = re.sub(
        r'(    qr_code TEXT,)\s+(    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,)\s+(    paid_at TIMESTAMP,)',
        r'\1\n    expires_at TIMESTAMP,\n    \2\n    confirmed_at TIMESTAMP,',
        content
    )

    content = re.sub(
        r'    cancelled_at TIMESTAMP,\s+    refund_id VARCHAR\(255\),\s+    refunded_at TIMESTAMP,\s+    notes TEXT,',
        '    cancelled_at TIMESTAMP,',
        content
    )

    content = re.sub(
        r"status IN \('PENDING', 'CONFIRMED', 'CANCELLED', 'FAILED', 'REFUNDED'\)",
        "status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED')",
        content
    )

    content = re.sub(
        r"CONSTRAINT bookings_payment_consistency CHECK \(\s+\(status = 'CONFIRMED' AND paid_at IS NOT NULL\) OR\s+\(status != 'CONFIRMED'\)\s+\),",
        "CONSTRAINT bookings_payment_consistency CHECK (\n        (status = 'CONFIRMED' AND confirmed_at IS NOT NULL) OR\n        (status != 'CONFIRMED')\n    ),",
        content
    )

    content = re.sub(
        r'CREATE INDEX idx_bookings_paid_at ON bookings\(paid_at\);',
        'CREATE INDEX idx_bookings_confirmed_at ON bookings(confirmed_at);',
        content
    )

    content = re.sub(
        r"COMMENT ON COLUMN bookings\.status IS 'PENDING, CONFIRMED, CANCELLED, FAILED, REFUNDED';",
        "COMMENT ON COLUMN bookings.booking_code IS 'Unique booking reference code';\nCOMMENT ON COLUMN bookings.expires_at IS 'Expiration time for pending bookings';\nCOMMENT ON COLUMN bookings.status IS 'PENDING, CONFIRMED, CANCELLED, EXPIRED';",
        content
    )

    # 9. Booking_Seats Table - change booking_seat_id to id
    content = re.sub(
        r'CREATE TABLE booking_seats \(\s+booking_seat_id BIGSERIAL PRIMARY KEY,',
        'CREATE TABLE booking_seats (\n    id BIGSERIAL PRIMARY KEY,',
        content
    )

    # 10. Genres Table - change genre_id to id
    content = re.sub(
        r'CREATE TABLE genres \(\s+genre_id SERIAL PRIMARY KEY,',
        'CREATE TABLE genres (\n    id SERIAL PRIMARY KEY,',
        content
    )

    # 11. Payments Table - major simplification
    payments_old = r'CREATE TABLE payments \(\s+payment_id BIGSERIAL PRIMARY KEY,.*?(?=\n-- Indexes)'
    payments_new = """CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE RESTRICT,
    payment_method VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(255),
    payment_url TEXT,
    callback_data TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,

    CONSTRAINT payments_amount_positive CHECK (amount > 0),
    CONSTRAINT payments_status_valid CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    CONSTRAINT payments_method_valid CHECK (payment_method IN ('VNPAY', 'MOMO', 'ZALOPAY', 'CASH'))
);
"""

    content = re.sub(payments_old, payments_new, content, flags=re.DOTALL)

    content = re.sub(
        r'-- Comments\nCOMMENT ON TABLE payments IS \'Payment transactions for bookings\';\nCOMMENT ON COLUMN payments\.payment_method IS \'VNPAY, MOMO, ZALOPAY, CARD, CASH, BANK_TRANSFER\';\nCOMMENT ON COLUMN payments\.payment_provider IS \'Payment gateway: VNPAY, MOMO, ZALOPAY, STRIPE, INTERNAL\';',
        "-- Comments\nCOMMENT ON TABLE payments IS 'Payment transactions for bookings';\nCOMMENT ON COLUMN payments.payment_method IS 'Payment method: VNPAY, MOMO, ZALOPAY, CASH';\nCOMMENT ON COLUMN payments.callback_data IS 'Payment gateway callback data as TEXT';\nCOMMENT ON COLUMN payments.failed_at IS 'Timestamp when payment failed';\nCOMMENT ON COLUMN payments.failure_reason IS 'Reason for payment failure';",
        content
    )

    # Update ER Diagram
    content = re.sub(r'│ PK: user_id  │', '│ PK: id       │', content)
    content = re.sub(r'│ PK: movie_id│', '│ PK: id      │', content)
    content = re.sub(r'│ FK: movie_id│', '│ FK: movie_id│', content)  # Keep FK names as is

    # Update trigger functions to use new column names
    content = re.sub(
        r'INSERT INTO show_seats \(show_id, seat_id, price, status\)\s+VALUES \(NEW\.show_id, seat_record\.seat_id',
        'INSERT INTO show_seats (show_id, seat_id, price, status)\n        VALUES (NEW.id, seat_record.id',
        content
    )

    # Write updated content
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

    print("Document updated successfully!")

if __name__ == "__main__":
    update_document("D:/work-space/cinema-booking/docs/05_THIET_KE_CO_SO_DU_LIEU.md")
