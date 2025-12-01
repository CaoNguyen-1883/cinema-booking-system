-- Bookings Table
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_code VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    show_id BIGINT NOT NULL REFERENCES shows(id) ON DELETE RESTRICT,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    final_amount DECIMAL(10, 2) NOT NULL,
    points_used INTEGER DEFAULT 0,
    points_earned INTEGER DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    qr_code TEXT,
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    cancelled_at TIMESTAMP,

    CONSTRAINT bookings_total_amount_positive CHECK (total_amount > 0),
    CONSTRAINT bookings_final_amount_non_negative CHECK (final_amount >= 0),
    CONSTRAINT bookings_discount_non_negative CHECK (discount_amount >= 0),
    CONSTRAINT bookings_points_non_negative CHECK (points_used >= 0 AND points_earned >= 0),
    CONSTRAINT bookings_status_valid CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED'))
);

CREATE INDEX idx_bookings_code ON bookings(booking_code);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_show_id ON bookings(show_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_created_at ON bookings(created_at);
CREATE INDEX idx_bookings_expires_at ON bookings(expires_at) WHERE status = 'PENDING';

COMMENT ON TABLE bookings IS 'Ticket bookings';
COMMENT ON COLUMN bookings.booking_code IS 'Unique booking reference code';

-- Booking_Seats Table
CREATE TABLE booking_seats (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    show_seat_id BIGINT NOT NULL REFERENCES show_seats(id) ON DELETE RESTRICT,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT booking_seats_price_positive CHECK (price > 0),
    UNIQUE (booking_id, show_seat_id)
);

CREATE INDEX idx_booking_seats_booking_id ON booking_seats(booking_id);
CREATE INDEX idx_booking_seats_show_seat_id ON booking_seats(show_seat_id);

COMMENT ON TABLE booking_seats IS 'Junction table linking bookings to specific seats';

-- Payments Table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    payment_method VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_id VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_url TEXT,
    callback_data TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,

    CONSTRAINT payments_amount_positive CHECK (amount > 0),
    CONSTRAINT payments_method_valid CHECK (payment_method IN ('VNPAY', 'MOMO', 'ZALOPAY', 'CASH')),
    CONSTRAINT payments_status_valid CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED'))
);

CREATE INDEX idx_payments_booking_id ON payments(booking_id);
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);
CREATE INDEX idx_payments_status ON payments(status);

COMMENT ON TABLE payments IS 'Payment transactions';
