# CHƯƠNG 5: THIẾT KẾ CƠ SỞ DỮ LIỆU

## Giới thiệu

Cơ sở dữ liệu là thành phần quan trọng nhất của hệ thống, lưu trữ toàn bộ dữ liệu nghiệp vụ và đảm bảo tính nhất quán, toàn vẹn của thông tin. Chương này trình bày thiết kế chi tiết cơ sở dữ liệu cho hệ thống đặt vé rạp chiếu phim, bao gồm:

1. **Lược đồ quan hệ (ER Diagram):** Mô hình hóa các thực thể và mối quan hệ
2. **Thiết kế bảng (Table Schema):** Định nghĩa cấu trúc từng bảng với kiểu dữ liệu, constraints
3. **Quan hệ và ràng buộc:** Foreign keys, unique constraints, check constraints
4. **Chiến lược đánh chỉ mục:** Indexing để tối ưu performance
5. **Redis Data Structures:** Caching và distributed locking
6. **Database Optimization:** Partitioning, normalization, denormalization

Hệ thống sử dụng hai loại database:
- **PostgreSQL 15:** Relational database cho dữ liệu structured
- **Redis 7:** In-memory database cho caching, session, distributed locks

Các nguyên tắc thiết kế:
- **Normalization:** Tuân thủ 3NF (Third Normal Form) để tránh data redundancy
- **ACID:** Đảm bảo Atomicity, Consistency, Isolation, Durability
- **Referential Integrity:** Sử dụng foreign keys để đảm bảo tính toàn vẹn tham chiếu
- **Performance:** Indexing hợp lý, denormalization khi cần thiết
- **Scalability:** Thiết kế cho phép horizontal scaling trong tương lai

---

## 5.1 Lược đồ quan hệ (ER Diagram)

### 5.1.1 ER Diagram tổng quan

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Entity Relationship Diagram                      │
└─────────────────────────────────────────────────────────────────────┘


                         ┌──────────────┐
                         │    USERS     │
                         ├──────────────┤
                         │ PK: id       │
                         │    email     │
                         │    password  │
                         │    full_name │
                         │    points    │
                         └──────┬───────┘
                                │
                                │ 1
                                │
                                │ makes
                                │
                                │ N
                         ┌──────▼───────┐
                         │   BOOKINGS   │
                         ├──────────────┤
                         │ PK: id       │
                         │ FK: user_id  │
                         │ FK: show_id  │
                         │ total_amount │
                         │ status       │
                         │ created_at   │
                         └──────┬───────┘
                                │
                    ┌───────────┼───────────┐
                    │ N         │           │ 1
                    │           │           │
              references    contains   references
                    │           │           │
                    │ N         │ N         │ 1
             ┌──────▼─────┐ ┌──▼───────┐ ┌─▼──────────┐
             │ SHOW_SEATS │ │ BOOKING_ │ │   SHOWS    │
             │            │ │  SEATS   │ ├────────────┤
             ├────────────┤ ├──────────┤ │ PK: id     │
             │ PK: id     │ │ PK: id   │ │ FK: movie_id│
             │ FK: show_id│ │ FK: booking│ FK: hall_id│
             │ FK: seat_id│ │ FK: show_seat│ show_date│
             │ price      │ │ price    │ │ show_time  │
             │ status     │ └──────────┘ │ base_price │
             └──────┬─────┘              └─┬────────┬─┘
                    │                      │ 1      │ 1
                    │ N                    │        │
                    │                  shown_in  features
                    │                      │        │
                    │ 1                    │ N      │ N
             ┌──────▼─────┐         ┌─────▼──┐ ┌──▼──────┐
             │   SEATS    │         │ HALLS  │ │ MOVIES  │
             ├────────────┤         ├────────┤ ├─────────┤
             │ PK: id     │         │ PK: id │ │ PK: id  │
             │ FK: hall_id│         │ FK: cinema│ title  │
             │ row_number │         │ name   │ │ director│
             │ seat_number│         │ capacity│ duration│
             │ seat_type  │         └─┬──────┘ │ genre   │
             └────────────┘           │ N      │ rating  │
                                      │        └─────────┘
                                 located_in
                                      │
                                      │ 1
                               ┌──────▼───────┐
                               │   CINEMAS    │
                               ├──────────────┤
                               │ PK: id       │
                               │    name      │
                               │    address   │
                               │    city      │
                               │    phone     │
                               └──────────────┘


                         ┌──────────────┐
                         │  GENRES      │
                         ├──────────────┤         Many-to-Many
                         │ PK: id       │◄──────────────┐
                         │    name      │                │
                         └──────────────┘                │
                                                         │
                                                  ┌──────▼──────┐
                                                  │ MOVIE_GENRES│
                                                  ├─────────────┤
                                                  │ FK: movie_id│
                                                  │ FK: genre_id│
                                                  └─────────────┘
```

### 5.1.2 Cardinality và Relationships

**1. User - Booking (1:N)**
- Một User có thể có nhiều Bookings
- Một Booking thuộc về một User duy nhất
- Relationship: `users.id` ← `bookings.user_id`

**2. Show - Booking (1:N)**
- Một Show có thể có nhiều Bookings
- Một Booking chỉ cho một Show duy nhất
- Relationship: `shows.id` ← `bookings.show_id`

**3. Booking - BookingSeat (1:N)**
- Một Booking có nhiều BookingSeats (many tickets)
- Một BookingSeat thuộc về một Booking duy nhất
- Relationship: `bookings.id` ← `booking_seats.booking_id`

**4. Show - ShowSeat (1:N)**
- Một Show có nhiều ShowSeats (all seats in hall for that show)
- Một ShowSeat thuộc về một Show duy nhất
- Relationship: `shows.id` ← `show_seats.show_id`

**5. Seat - ShowSeat (1:N)**
- Một Seat (physical seat) xuất hiện trong nhiều ShowSeats (different shows)
- Một ShowSeat tham chiếu đến một Seat duy nhất
- Relationship: `seats.id` ← `show_seats.seat_id`

**6. Movie - Show (1:N)**
- Một Movie có nhiều Shows (different times, halls, dates)
- Một Show chiếu một Movie duy nhất
- Relationship: `movies.id` ← `shows.movie_id`

**7. Hall - Show (1:N)**
- Một Hall có nhiều Shows (different times, dates)
- Một Show diễn ra trong một Hall duy nhất
- Relationship: `halls.id` ← `shows.hall_id`

**8. Hall - Seat (1:N)**
- Một Hall có nhiều Seats (physical seats)
- Một Seat thuộc về một Hall duy nhất
- Relationship: `halls.id` ← `seats.hall_id`

**9. Cinema - Hall (1:N)**
- Một Cinema có nhiều Halls
- Một Hall thuộc về một Cinema duy nhất
- Relationship: `cinemas.id` ← `halls.cinema_id`

**10. Movie - Genre (N:M)**
- Một Movie có nhiều Genres
- Một Genre có nhiều Movies
- Junction table: `movie_genres` (movie_id, genre_id)

---

## 5.2 Thiết kế bảng (Table Schema)

### 5.2.1 Users Table

Lưu thông tin tài khoản người dùng.

**DDL:**

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    avatar_url VARCHAR(500),
    points INTEGER NOT NULL DEFAULT 0,
    token_version BIGINT NOT NULL DEFAULT 0,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,
    last_login_at TIMESTAMP,

    CONSTRAINT users_points_non_negative CHECK (points >= 0),
    CONSTRAINT users_role_valid CHECK (role IN ('CUSTOMER', 'STAFF', 'ADMIN')),
    CONSTRAINT users_status_valid CHECK (status IN ('ACTIVE', 'INACTIVE', 'BANNED'))
);

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Comments
COMMENT ON TABLE users IS 'User accounts table';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';
COMMENT ON COLUMN users.avatar_url IS 'URL to user profile avatar image';
COMMENT ON COLUMN users.points IS 'Loyalty points (1000 VND = 1 point)';
COMMENT ON COLUMN users.token_version IS 'Version number for JWT token invalidation';
COMMENT ON COLUMN users.role IS 'User role: CUSTOMER, STAFF, ADMIN';
COMMENT ON COLUMN users.status IS 'Account status: ACTIVE, INACTIVE, BANNED';
```

**Sample Data:**

```sql
INSERT INTO users (email, username, password_hash, full_name, phone_number, points, role, status)
VALUES
('john.doe@example.com', 'john_doe', '$2a$10$...', 'John Doe', '0901234567', 150, 'CUSTOMER', 'ACTIVE'),
('admin@cinema.com', 'admin', '$2a$10$...', 'System Admin', '0987654321', 0, 'ADMIN', 'ACTIVE');
```

### 5.2.2 Movies Table

Lưu thông tin phim.

**DDL:**

```sql
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
    status VARCHAR(20) NOT NULL DEFAULT 'NOW_SHOWING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,

    CONSTRAINT movies_duration_positive CHECK (duration > 0),
    CONSTRAINT movies_rating_valid CHECK (rating IN ('P', 'K', 'T13', 'T16', 'T18', 'C')),
    CONSTRAINT movies_status_valid CHECK (status IN ('NOW_SHOWING', 'COMING_SOON', 'ENDED'))
);

-- Indexes
CREATE INDEX idx_movies_status ON movies(status);
CREATE INDEX idx_movies_release_date ON movies(release_date);
CREATE INDEX idx_movies_title ON movies USING gin(to_tsvector('english', title));

-- Full-text search index
CREATE INDEX idx_movies_search ON movies USING gin(
    to_tsvector('english', title || ' ' || COALESCE(original_title, '') || ' ' || director)
);

-- Comments
COMMENT ON TABLE movies IS 'Movies catalog';
COMMENT ON COLUMN movies.cast_members IS 'Comma-separated list of cast members';
COMMENT ON COLUMN movies.banner_url IS 'URL to movie banner image for headers';
COMMENT ON COLUMN movies.end_date IS 'Date when movie stops showing';
COMMENT ON COLUMN movies.duration IS 'Duration in minutes';
COMMENT ON COLUMN movies.rating IS 'Age rating: P(All ages), K(Kids), T13(13+), T16(16+), T18(18+), C(Restricted)';
COMMENT ON COLUMN movies.status IS 'NOW_SHOWING, COMING_SOON, ENDED';
```

**Sample Data:**

```sql
INSERT INTO movies (title, director, genre, duration, release_date, rating, description, status)
VALUES
('Avatar: The Way of Water', 'James Cameron', 'Action, Sci-Fi', 192, '2025-01-10', 'T13',
 'Set more than a decade after the events of the first film...', 'NOW_SHOWING'),
('Oppenheimer', 'Christopher Nolan', 'Biography, Drama, History', 180, '2025-01-05', 'T16',
 'The story of American scientist J. Robert Oppenheimer...', 'NOW_SHOWING');
```

### 5.2.3 Cinemas Table

Lưu thông tin rạp chiếu phim.

**DDL:**

```sql
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
    confirmed_at TIMESTAMP,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,

    CONSTRAINT cinemas_status_valid CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE'))
);

-- Indexes
CREATE INDEX idx_cinemas_city ON cinemas(city);
CREATE INDEX idx_cinemas_status ON cinemas(status);
CREATE INDEX idx_cinemas_name ON cinemas USING gin(to_tsvector('english', name));

-- Comments
COMMENT ON TABLE cinemas IS 'Cinema locations';
COMMENT ON COLUMN cinemas.facilities IS 'Comma-separated facilities: parking, food_court, 3D, IMAX, etc.';
```

**Sample Data:**

```sql
INSERT INTO cinemas (name, address, city, district, phone_number, facilities, status)
VALUES
('CGV Vincom Center', '72 Le Thanh Ton, Ben Nghe Ward', 'Ho Chi Minh City', 'District 1', '1900545463',
 'Parking,Food Court,3D,IMAX', 'ACTIVE'),
('CGV Aeon Tan Phu', '30 Bo Bao Tan Thang, Son Ky Ward', 'Ho Chi Minh City', 'Tan Phu District', '1900545463',
 'Parking,Food Court,3D', 'ACTIVE'),
('CGV Vincom Ha Long', '206C Ha Long, Bach Dang Ward', 'Quang Ninh', 'Ha Long City', '1900545463',
 'Parking,Food Court,3D', 'ACTIVE');
```

### 5.2.4 Halls Table

Lưu thông tin phòng chiếu.

**DDL:**

```sql
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
    confirmed_at TIMESTAMP,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,

    CONSTRAINT halls_total_seats_positive CHECK (total_seats > 0),
    CONSTRAINT halls_hall_type_valid CHECK (hall_type IN ('STANDARD', 'VIP', 'IMAX', 'THREE_D', 'FOUR_DX')),
    CONSTRAINT halls_status_valid CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE')),
    UNIQUE (cinema_id, name)
);

-- Indexes
CREATE INDEX idx_halls_cinema_id ON halls(cinema_id);
CREATE INDEX idx_halls_status ON halls(status);
CREATE INDEX idx_halls_hall_type ON halls(hall_type);

-- Comments
COMMENT ON TABLE halls IS 'Cinema halls/screens';
COMMENT ON COLUMN halls.total_rows IS 'Number of seat rows in the hall';
COMMENT ON COLUMN halls.seats_per_row IS 'Number of seats per row';
COMMENT ON COLUMN halls.hall_type IS 'STANDARD, VIP, IMAX, THREE_D, FOUR_DX';
```

**Sample Data:**

```sql
INSERT INTO halls (cinema_id, name, hall_type, total_seats, seat_layout)
VALUES
(1, 'Hall 1', 'STANDARD', 100, '{"rows": 10, "columns": 10, "aisles": [5]}'),
(1, 'Hall 2', 'VIP', 50, '{"rows": 5, "columns": 10, "aisles": []}'),
(1, 'Hall 3', 'IMAX', 120, '{"rows": 12, "columns": 10, "aisles": [6]}');
```

### 5.2.5 Seats Table

Lưu thông tin ghế ngồi (physical seats).

**DDL:**

```sql
CREATE TABLE seats (
    id BIGSERIAL PRIMARY KEY,
    hall_id BIGINT NOT NULL REFERENCES halls(id) ON DELETE CASCADE,
    row_name VARCHAR(5) NOT NULL,
    seat_number VARCHAR(5) NOT NULL,
    seat_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,

    CONSTRAINT seats_seat_type_valid CHECK (seat_type IN ('NORMAL', 'VIP', 'COUPLE')),
    CONSTRAINT seats_status_valid CHECK (status IN ('ACTIVE', 'INACTIVE', 'BROKEN')),
    UNIQUE (hall_id, row_name, seat_number)
);

-- Indexes
CREATE INDEX idx_seats_hall_id ON seats(hall_id);
CREATE INDEX idx_seats_status ON seats(status);
CREATE INDEX idx_seats_type ON seats(seat_type);
CREATE UNIQUE INDEX idx_seats_unique_position ON seats(hall_id, row_name, seat_number);

-- Comments
COMMENT ON TABLE seats IS 'Physical seats in halls';
COMMENT ON COLUMN seats.seat_type IS 'NORMAL, VIP, COUPLE';
COMMENT ON COLUMN seats.row_name IS 'Row identifier (A, B, C, etc.)';
```

**Sample Data:**

```sql
-- Generate 100 seats for Hall 1 (10 rows x 10 columns)
INSERT INTO seats (hall_id, row_number, seat_number, seat_type)
SELECT
    1 as hall_id,
    chr(64 + row_num) as row_number,  -- A, B, C, ...
    seat_num::VARCHAR as seat_number,
    CASE
        WHEN row_num <= 2 THEN 'VIP'
        WHEN row_num <= 5 THEN 'PREMIUM'
        ELSE 'NORMAL'
    END as seat_type
FROM
    generate_series(1, 10) as row_num,
    generate_series(1, 10) as seat_num;
```

### 5.2.6 Shows Table

Lưu thông tin suất chiếu.

**DDL:**

```sql
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
    confirmed_at TIMESTAMP,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,

    CONSTRAINT shows_base_price_positive CHECK (base_price > 0),
    CONSTRAINT shows_status_valid CHECK (status IN ('SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT shows_future_date CHECK (show_date >= CURRENT_DATE OR status = 'COMPLETED')
);

-- Indexes
CREATE INDEX idx_shows_movie_id ON shows(movie_id);
CREATE INDEX idx_shows_hall_id ON shows(hall_id);
CREATE INDEX idx_shows_date ON shows(show_date);
CREATE INDEX idx_shows_time ON shows(show_time);
CREATE INDEX idx_shows_status ON shows(status);
CREATE INDEX idx_shows_date_time ON shows(show_date, show_time);
CREATE INDEX idx_shows_movie_date ON shows(movie_id, show_date);

-- Composite index for common queries
CREATE INDEX idx_shows_lookup ON shows(movie_id, show_date, status);

-- Comments
COMMENT ON TABLE shows IS 'Movie show schedules';
COMMENT ON COLUMN shows.base_price IS 'Base ticket price in VND';
COMMENT ON COLUMN shows.start_time IS 'Show start time';
COMMENT ON COLUMN shows.end_time IS 'Show end time (calculated from start_time + movie duration)';
COMMENT ON COLUMN shows.status IS 'SCHEDULED, ONGOING, COMPLETED, CANCELLED';
```

**Sample Data:**

```sql
INSERT INTO shows (movie_id, hall_id, show_date, show_time, base_price, status)
VALUES
(1, 1, '2025-01-16', '14:00', 80000, 'SCHEDULED'),
(1, 1, '2025-01-16', '18:00', 90000, 'SCHEDULED'),
(1, 2, '2025-01-16', '20:00', 120000, 'SCHEDULED'),
(2, 3, '2025-01-16', '15:00', 100000, 'SCHEDULED');
```

### 5.2.7 Show_Seats Table

Lưu trạng thái ghế cho từng suất chiếu.

**DDL:**

```sql
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
    confirmed_at TIMESTAMP,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,

    CONSTRAINT show_seats_price_positive CHECK (price > 0),
    CONSTRAINT show_seats_status_valid CHECK (status IN ('AVAILABLE', 'LOCKED', 'SOLD')),
    CONSTRAINT show_seats_lock_consistency CHECK (
        (status = 'LOCKED' AND locked_by IS NOT NULL) OR
        (status != 'LOCKED' AND locked_by IS NULL)
    ),
    UNIQUE (show_id, seat_id)
);

-- Indexes
CREATE INDEX idx_show_seats_show_id ON show_seats(show_id);
CREATE INDEX idx_show_seats_seat_id ON show_seats(seat_id);
CREATE INDEX idx_show_seats_status ON show_seats(status);
CREATE INDEX idx_show_seats_locked_by ON show_seats(locked_by);
CREATE INDEX idx_show_seats_locked_at ON show_seats(locked_at) WHERE status = 'LOCKED';
CREATE UNIQUE INDEX idx_show_seats_unique ON show_seats(show_id, seat_id);

-- Composite index for seat availability check
CREATE INDEX idx_show_seats_availability ON show_seats(show_id, status);

-- Comments
COMMENT ON TABLE show_seats IS 'Seat availability for each show';
COMMENT ON COLUMN show_seats.price IS 'Final price for this seat (base_price + seat_type multiplier)';
COMMENT ON COLUMN show_seats.status IS 'AVAILABLE, LOCKED, SOLD';
COMMENT ON COLUMN show_seats.locked_by IS 'User who locked this seat (5 min TTL)';
COMMENT ON COLUMN show_seats.locked_at IS 'Timestamp when seat was locked';
```

**Trigger để tự động tạo show_seats khi tạo show:**

```sql
CREATE OR REPLACE FUNCTION create_show_seats()
RETURNS TRIGGER AS $$
DECLARE
    seat_record RECORD;
    calculated_price DECIMAL(10, 2);
BEGIN
    -- Create show_seats for all seats in the hall
    FOR seat_record IN
        SELECT seat_id, seat_type
        FROM seats
        WHERE hall_id = NEW.hall_id
        AND status = 'ACTIVE'
    LOOP
        -- Calculate price based on seat type
        calculated_price := NEW.base_price;

        IF seat_record.seat_type = 'VIP' THEN
            calculated_price := NEW.base_price * 1.5;
        ELSIF seat_record.seat_type = 'PREMIUM' THEN
            calculated_price := NEW.base_price * 1.2;
        ELSIF seat_record.seat_type = 'COUPLE' THEN
            calculated_price := NEW.base_price * 2;
        END IF;

        -- Insert show_seat
        INSERT INTO show_seats (show_id, seat_id, price, status)
        VALUES (NEW.id, seat_record.id, calculated_price, 'AVAILABLE');
    END LOOP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_create_show_seats
AFTER INSERT ON shows
FOR EACH ROW
EXECUTE FUNCTION create_show_seats();
```

### 5.2.8 Bookings Table

Lưu thông tin đặt vé.

**DDL:**

```sql
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
    cancelled_at TIMESTAMP,
    confirmed_at TIMESTAMP,

    CONSTRAINT bookings_total_amount_positive CHECK (total_amount > 0),
    CONSTRAINT bookings_final_amount_positive CHECK (final_amount >= 0),
    CONSTRAINT bookings_discount_non_negative CHECK (discount_amount >= 0),
    CONSTRAINT bookings_points_used_non_negative CHECK (points_used >= 0),
    CONSTRAINT bookings_points_earned_non_negative CHECK (points_earned >= 0),
    CONSTRAINT bookings_status_valid CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED')),
    CONSTRAINT bookings_payment_consistency CHECK (
        (status = 'CONFIRMED' AND paid_at IS NOT NULL) OR
        (status != 'CONFIRMED')
    ),
    CONSTRAINT bookings_cancellation_consistency CHECK (
        (status IN ('CANCELLED', 'REFUNDED') AND cancelled_at IS NOT NULL) OR
        (status NOT IN ('CANCELLED', 'REFUNDED'))
    )
);

-- Indexes
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_show_id ON bookings(show_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_created_at ON bookings(created_at);
CREATE INDEX idx_bookings_paid_at ON bookings(paid_at);
CREATE INDEX idx_bookings_transaction_id ON bookings(transaction_id);

-- Composite indexes for common queries
CREATE INDEX idx_bookings_user_status ON bookings(user_id, status);
CREATE INDEX idx_bookings_user_created ON bookings(user_id, created_at DESC);
CREATE INDEX idx_bookings_timeout ON bookings(status, created_at)
    WHERE status = 'PENDING';

-- Comments
COMMENT ON TABLE bookings IS 'Ticket bookings';
COMMENT ON COLUMN bookings.total_amount IS 'Sum of all seat prices';
COMMENT ON COLUMN bookings.discount_amount IS 'Discount from points or promotions';
COMMENT ON COLUMN bookings.final_amount IS 'Amount actually paid (total - discount)';
COMMENT ON COLUMN bookings.points_used IS 'Loyalty points used for discount';
COMMENT ON COLUMN bookings.points_earned IS 'Loyalty points earned from this booking';
COMMENT ON COLUMN bookings.booking_code IS 'Unique booking reference code';
COMMENT ON COLUMN bookings.expires_at IS 'Expiration time for pending bookings';
COMMENT ON COLUMN bookings.status IS 'PENDING, CONFIRMED, CANCELLED, EXPIRED';
COMMENT ON COLUMN bookings.qr_code IS 'Base64 encoded QR code for ticket verification';
```

### 5.2.9 Booking_Seats Table

Lưu chi tiết ghế đã đặt trong mỗi booking.

**DDL:**

```sql
CREATE TABLE booking_seats (
    id BIGSERIAL PRIMARY KEY,
    booking_code VARCHAR(20) NOT NULL UNIQUE,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    show_seat_id BIGINT NOT NULL REFERENCES show_seats(id) ON DELETE RESTRICT,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    confirmed_at TIMESTAMP,
    CONSTRAINT booking_seats_price_positive CHECK (price > 0),
    UNIQUE (booking_id, show_seat_id)
);

-- Indexes
CREATE INDEX idx_booking_seats_booking_id ON booking_seats(booking_id);
CREATE INDEX idx_booking_seats_show_seat_id ON booking_seats(show_seat_id);
CREATE UNIQUE INDEX idx_booking_seats_unique ON booking_seats(booking_id, show_seat_id);

-- Comments
COMMENT ON TABLE booking_seats IS 'Junction table linking bookings to specific seats';
COMMENT ON COLUMN booking_seats.price IS 'Price snapshot at booking time';
```

### 5.2.10 Genres Table

Lưu danh mục thể loại phim.

**DDL:**

```sql
CREATE TABLE genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE UNIQUE INDEX idx_genres_name ON genres(name);
CREATE UNIQUE INDEX idx_genres_slug ON genres(slug);

-- Comments
COMMENT ON TABLE genres IS 'Movie genres/categories';
COMMENT ON COLUMN genres.slug IS 'URL-friendly version of name';

-- Sample data
INSERT INTO genres (name, slug, description) VALUES
('Action', 'action', 'Action-packed movies with intense sequences'),
('Comedy', 'comedy', 'Movies designed to make you laugh'),
('Drama', 'drama', 'Serious narrative driven movies'),
('Horror', 'horror', 'Scary movies designed to frighten'),
('Sci-Fi', 'sci-fi', 'Science fiction movies'),
('Romance', 'romance', 'Love stories and romantic comedies'),
('Thriller', 'thriller', 'Suspenseful movies that keep you on edge'),
('Animation', 'animation', 'Animated movies for all ages'),
('Documentary', 'documentary', 'Non-fiction educational movies'),
('Fantasy', 'fantasy', 'Magical and fantastical stories');
```

### 5.2.11 Movie_Genres Table

Junction table cho many-to-many relationship giữa Movies và Genres.

**DDL:**

```sql
CREATE TABLE movie_genres (
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    genre_id INTEGER NOT NULL REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, genre_id)
);

-- Indexes
CREATE INDEX idx_movie_genres_movie_id ON movie_genres(movie_id);
CREATE INDEX idx_movie_genres_genre_id ON movie_genres(genre_id);

-- Comments
COMMENT ON TABLE movie_genres IS 'Many-to-many relationship between movies and genres';
```

### 5.2.12 Payments Table

Lưu thông tin thanh toán chi tiết cho mỗi booking.

**DDL:**

```sql
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE RESTRICT,
    payment_method VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_url TEXT,
    callback_data TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,

    CONSTRAINT payments_amount_positive CHECK (amount > 0),
    CONSTRAINT payments_status_valid CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    CONSTRAINT payments_method_valid CHECK (payment_method IN ('VNPAY', 'MOMO', 'ZALOPAY', 'CASH')),
);

-- Indexes
CREATE INDEX idx_payments_booking_id ON payments(booking_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);
CREATE INDEX idx_payments_booking_status ON payments(booking_id, status);

-- Comments
COMMENT ON TABLE payments IS 'Payment transactions for bookings';
COMMENT ON COLUMN payments.payment_method IS 'Payment method: VNPAY, MOMO, ZALOPAY, CASH';
COMMENT ON COLUMN payments.callback_data IS 'Payment gateway callback data stored as TEXT';
COMMENT ON COLUMN payments.failed_at IS 'Timestamp when payment failed';
COMMENT ON COLUMN payments.failure_reason IS 'Reason for payment failure';
```

**Trigger cập nhật booking khi payment hoàn thành:**

```sql
CREATE OR REPLACE FUNCTION update_booking_on_payment()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'COMPLETED' AND OLD.status != 'COMPLETED' THEN
        UPDATE bookings SET status = 'CONFIRMED', confirmed_at = NEW.paid_at
        WHERE id = NEW.booking_id;
        UPDATE show_seats ss SET status = 'SOLD'
        FROM booking_seats bs WHERE bs.booking_id = NEW.booking_id AND ss.show_seat_id = bs.show_seat_id;
    END IF;
    IF NEW.status IN ('FAILED', 'EXPIRED') AND OLD.status = 'PENDING' THEN
        UPDATE bookings SET status = 'EXPIRED' WHERE id = NEW.booking_id;
        UPDATE show_seats ss SET status = 'AVAILABLE', locked_by = NULL
        FROM booking_seats bs WHERE bs.booking_id = NEW.booking_id AND ss.show_seat_id = bs.show_seat_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_booking_on_payment
AFTER UPDATE ON payments FOR EACH ROW
WHEN (OLD.status IS DISTINCT FROM NEW.status)
EXECUTE FUNCTION update_booking_on_payment();
```


---

## 5.3 Quan hệ và ràng buộc (Relationships & Constraints)

### 5.3.1 Foreign Key Constraints

**1. Users → Bookings**
```sql
ALTER TABLE bookings
ADD CONSTRAINT fk_bookings_user
FOREIGN KEY (user_id) REFERENCES users(id)
ON DELETE RESTRICT  -- Cannot delete user if they have bookings
ON UPDATE CASCADE;
```

**2. Shows → Bookings**
```sql
ALTER TABLE bookings
ADD CONSTRAINT fk_bookings_show
FOREIGN KEY (show_id) REFERENCES shows(id)
ON DELETE RESTRICT  -- Cannot delete show if it has bookings
ON UPDATE CASCADE;
```

**3. Movies → Shows**
```sql
ALTER TABLE shows
ADD CONSTRAINT fk_shows_movie
FOREIGN KEY (movie_id) REFERENCES movies(id)
ON DELETE RESTRICT  -- Cannot delete movie if it has scheduled shows
ON UPDATE CASCADE;
```

**4. Halls → Shows**
```sql
ALTER TABLE shows
ADD CONSTRAINT fk_shows_hall
FOREIGN KEY (hall_id) REFERENCES halls(id)
ON DELETE CASCADE  -- If hall deleted, all its shows are deleted
ON UPDATE CASCADE;
```

**5. Cinemas → Halls**
```sql
ALTER TABLE halls
ADD CONSTRAINT fk_halls_cinema
FOREIGN KEY (cinema_id) REFERENCES cinemas(id)
ON DELETE CASCADE  -- If cinema deleted, all its halls are deleted
ON UPDATE CASCADE;
```

**6. Halls → Seats**
```sql
ALTER TABLE seats
ADD CONSTRAINT fk_seats_hall
FOREIGN KEY (hall_id) REFERENCES halls(id)
ON DELETE CASCADE  -- If hall deleted, all its seats are deleted
ON UPDATE CASCADE;
```

**7. Shows + Seats → ShowSeats**
```sql
ALTER TABLE show_seats
ADD CONSTRAINT fk_show_seats_show
FOREIGN KEY (show_id) REFERENCES shows(id)
ON DELETE CASCADE;  -- If show deleted, all seat allocations deleted

ALTER TABLE show_seats
ADD CONSTRAINT fk_show_seats_seat
FOREIGN KEY (seat_id) REFERENCES seats(id)
ON DELETE CASCADE;  -- If seat deleted, all allocations deleted
```

**8. Bookings + ShowSeats → BookingSeats**
```sql
ALTER TABLE booking_seats
ADD CONSTRAINT fk_booking_seats_booking
FOREIGN KEY (booking_id) REFERENCES bookings(id)
ON DELETE CASCADE;  -- If booking deleted, all seat links deleted

ALTER TABLE booking_seats
ADD CONSTRAINT fk_booking_seats_show_seat
FOREIGN KEY (show_seat_id) REFERENCES show_seats(id)
ON DELETE RESTRICT;  -- Cannot delete show_seat if it's booked
```

### 5.3.2 Unique Constraints

```sql
-- No duplicate email or username
ALTER TABLE users
ADD CONSTRAINT uq_users_email UNIQUE (email);

ALTER TABLE users
ADD CONSTRAINT uq_users_username UNIQUE (username);

-- No duplicate hall names within same cinema
ALTER TABLE halls
ADD CONSTRAINT uq_halls_cinema_name UNIQUE (cinema_id, name);

-- No duplicate seat positions within same hall
ALTER TABLE seats
ADD CONSTRAINT uq_seats_hall_position UNIQUE (hall_id, row_name, seat_number);

-- No duplicate seat in same show
ALTER TABLE show_seats
ADD CONSTRAINT uq_show_seats_show_seat UNIQUE (show_id, seat_id);

-- No duplicate booking for same seat
ALTER TABLE booking_seats
ADD CONSTRAINT uq_booking_seats_booking_seat UNIQUE (booking_id, show_seat_id);
```

### 5.3.3 Check Constraints

**Business rules validation:**

```sql
-- Seat count must match number of actual seats
ALTER TABLE halls
ADD CONSTRAINT chk_halls_seat_count CHECK (
    total_seats = (
        SELECT COUNT(*)
        FROM seats
        WHERE seats.hall_id = halls.hall_id
        AND seats.status = 'ACTIVE'
    )
);

-- Booking final amount must equal total minus discount
ALTER TABLE bookings
ADD CONSTRAINT chk_bookings_amount_calculation CHECK (
    final_amount = total_amount - discount_amount
);

-- Cannot use more than 50% of total amount in points
ALTER TABLE bookings
ADD CONSTRAINT chk_bookings_max_points_discount CHECK (
    (points_used * 1000) <= (total_amount * 0.5)
);

-- Show time must be in valid hours (6 AM to 12 AM)
ALTER TABLE shows
ADD CONSTRAINT chk_shows_valid_time CHECK (
    show_time >= '06:00:00' AND show_time <= '23:59:59'
);

-- Show date cannot be more than 30 days in future
ALTER TABLE shows
ADD CONSTRAINT chk_shows_max_advance_booking CHECK (
    show_date <= CURRENT_DATE + INTERVAL '30 days'
);

-- Movie duration must be reasonable (30 min to 300 min)
ALTER TABLE movies
ADD CONSTRAINT chk_movies_duration_range CHECK (
    duration BETWEEN 30 AND 300
);
```

### 5.3.4 Complex Business Rules (Triggers)

**1. Prevent double booking:**

```sql
CREATE OR REPLACE FUNCTION prevent_double_booking()
RETURNS TRIGGER AS $$
BEGIN
    -- Check if seat is already SOLD for this show
    IF EXISTS (
        SELECT 1 FROM show_seats
        WHERE show_id = NEW.show_id
        AND seat_id = NEW.seat_id
        AND status = 'SOLD'
    ) THEN
        RAISE EXCEPTION 'Seat already sold for this show';
    END IF;

    -- Check if seat is LOCKED by another user
    IF EXISTS (
        SELECT 1 FROM show_seats
        WHERE show_id = NEW.show_id
        AND seat_id = NEW.seat_id
        AND status = 'LOCKED'
        AND locked_by != NEW.locked_by
        AND locked_at > NOW() - INTERVAL '5 minutes'
    ) THEN
        RAISE EXCEPTION 'Seat is currently locked by another user';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_prevent_double_booking
BEFORE INSERT OR UPDATE ON show_seats
FOR EACH ROW
EXECUTE FUNCTION prevent_double_booking();
```

**2. Auto-calculate booking amounts:**

```sql
CREATE OR REPLACE FUNCTION calculate_booking_amounts()
RETURNS TRIGGER AS $$
DECLARE
    seats_total DECIMAL(10, 2);
    points_discount DECIMAL(10, 2);
BEGIN
    -- Calculate total from all seats
    SELECT COALESCE(SUM(price), 0)
    INTO seats_total
    FROM booking_seats
    WHERE id = NEW.booking_id;

    NEW.total_amount := seats_total;

    -- Calculate points discount (1 point = 1000 VND)
    points_discount := NEW.points_used * 1000;

    -- Max discount is 50% of total
    IF points_discount > (seats_total * 0.5) THEN
        points_discount := seats_total * 0.5;
    END IF;

    NEW.discount_amount := points_discount;
    NEW.final_amount := seats_total - points_discount;

    -- Calculate points earned (1000 VND = 1 point)
    IF NEW.status = 'CONFIRMED' THEN
        NEW.points_earned := FLOOR(NEW.final_amount / 1000);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_calculate_booking_amounts
BEFORE INSERT OR UPDATE ON bookings
FOR EACH ROW
EXECUTE FUNCTION calculate_booking_amounts();
```

**3. Update user points on booking confirmation/cancellation:**

```sql
CREATE OR REPLACE FUNCTION update_user_points()
RETURNS TRIGGER AS $$
BEGIN
    -- Booking confirmed: add earned points, deduct used points
    IF NEW.status = 'CONFIRMED' AND OLD.status != 'CONFIRMED' THEN
        UPDATE users
        SET points = points + NEW.points_earned - NEW.points_used
        WHERE id = NEW.user_id;
    END IF;

    -- Booking cancelled: refund used points, deduct earned points
    IF NEW.status = 'CANCELLED' AND OLD.status = 'CONFIRMED' THEN
        UPDATE users
        SET points = points - NEW.points_earned + NEW.points_used
        WHERE id = NEW.user_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_user_points
AFTER UPDATE ON bookings
FOR EACH ROW
WHEN (OLD.status IS DISTINCT FROM NEW.status)
EXECUTE FUNCTION update_user_points();
```

**4. Prevent show scheduling conflicts:**

```sql
CREATE OR REPLACE FUNCTION check_show_conflict()
RETURNS TRIGGER AS $$
DECLARE
    movie_duration INTEGER;
    show_end_time TIME;
    conflict_count INTEGER;
BEGIN
    -- Get movie duration
    SELECT duration INTO movie_duration
    FROM movies
    WHERE movie_id = NEW.movie_id;

    -- Calculate show end time (duration + 30 min cleanup)
    show_end_time := NEW.show_time + (movie_duration + 30) * INTERVAL '1 minute';

    -- Check for conflicts in same hall on same date
    SELECT COUNT(*) INTO conflict_count
    FROM shows s
    JOIN movies m ON s.movie_id = m.movie_id
    WHERE s.hall_id = NEW.hall_id
    AND s.show_date = NEW.show_date
    AND s.show_id != COALESCE(NEW.show_id, 0)
    AND s.status != 'CANCELLED'
    AND (
        -- New show starts during existing show
        (NEW.show_time >= s.show_time AND NEW.show_time < s.show_time + (m.duration + 30) * INTERVAL '1 minute')
        OR
        -- New show ends during existing show
        (show_end_time > s.show_time AND show_end_time <= s.show_time + (m.duration + 30) * INTERVAL '1 minute')
        OR
        -- New show completely overlaps existing show
        (NEW.show_time <= s.show_time AND show_end_time >= s.show_time + (m.duration + 30) * INTERVAL '1 minute')
    );

    IF conflict_count > 0 THEN
        RAISE EXCEPTION 'Show schedule conflict detected in hall % on %', NEW.hall_id, NEW.show_date;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_show_conflict
BEFORE INSERT OR UPDATE ON shows
FOR EACH ROW
EXECUTE FUNCTION check_show_conflict();
```

---

## 5.4 Chiến lược đánh chỉ mục (Indexing Strategy)

### 5.4.1 Primary Key Indexes

Tất cả các bảng đều có PRIMARY KEY index (tự động tạo):

```sql
-- Automatically created by PRIMARY KEY constraint
CREATE UNIQUE INDEX users_pkey ON users(id);
CREATE UNIQUE INDEX movies_pkey ON movies(id);
CREATE UNIQUE INDEX cinemas_pkey ON cinemas(id);
CREATE UNIQUE INDEX halls_pkey ON halls(id);
CREATE UNIQUE INDEX seats_pkey ON seats(id);
CREATE UNIQUE INDEX shows_pkey ON shows(id);
CREATE UNIQUE INDEX show_seats_pkey ON show_seats(id);
CREATE UNIQUE INDEX bookings_pkey ON bookings(id);
CREATE UNIQUE INDEX booking_seats_pkey ON booking_seats(id);
```

### 5.4.2 Foreign Key Indexes

Indexes trên foreign key columns để tăng tốc JOIN operations:

```sql
-- Already created in section 5.2, listed here for reference

-- Users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

-- Bookings table
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_show_id ON bookings(show_id);

-- Shows table
CREATE INDEX idx_shows_movie_id ON shows(movie_id);
CREATE INDEX idx_shows_hall_id ON shows(hall_id);

-- Show_seats table
CREATE INDEX idx_show_seats_show_id ON show_seats(show_id);
CREATE INDEX idx_show_seats_seat_id ON show_seats(seat_id);

-- Booking_seats table
CREATE INDEX idx_booking_seats_booking_id ON booking_seats(booking_id);
CREATE INDEX idx_booking_seats_show_seat_id ON booking_seats(show_seat_id);

-- Halls table
CREATE INDEX idx_halls_cinema_id ON halls(cinema_id);

-- Seats table
CREATE INDEX idx_seats_hall_id ON seats(hall_id);
```

### 5.4.3 Query-Specific Indexes

**1. Search movies by title (Full-text search):**

```sql
CREATE INDEX idx_movies_search ON movies
USING gin(to_tsvector('english', title || ' ' || COALESCE(original_title, '') || ' ' || director));

-- Usage:
SELECT * FROM movies
WHERE to_tsvector('english', title || ' ' || COALESCE(original_title, '') || ' ' || director)
    @@ to_tsquery('english', 'avatar & water');
```

**2. Find available shows for a movie on a specific date:**

```sql
CREATE INDEX idx_shows_lookup ON shows(movie_id, show_date, status);

-- Usage:
SELECT * FROM shows
WHERE movie_id = 1
AND show_date = '2025-01-16'
AND status = 'SCHEDULED';
```

**3. Get booking history for user:**

```sql
CREATE INDEX idx_bookings_user_created ON bookings(user_id, created_at DESC);

-- Usage:
SELECT * FROM bookings
WHERE user_id = 123
ORDER BY created_at DESC
LIMIT 20;
```

**4. Find timeout bookings (for scheduler cleanup):**

```sql
CREATE INDEX idx_bookings_timeout ON bookings(status, created_at)
WHERE status = 'PENDING';

-- Usage:
SELECT * FROM bookings
WHERE status = 'PENDING'
AND created_at < NOW() - INTERVAL '15 minutes';
```

**5. Check seat availability for a show:**

```sql
CREATE INDEX idx_show_seats_availability ON show_seats(show_id, status);

-- Usage:
SELECT COUNT(*) FROM show_seats
WHERE show_id = 501
AND status = 'AVAILABLE';
```

**6. Find locked seats for cleanup:**

```sql
CREATE INDEX idx_show_seats_locked_at ON show_seats(locked_at)
WHERE status = 'LOCKED';

-- Usage:
SELECT * FROM show_seats
WHERE status = 'LOCKED'
AND locked_at < NOW() - INTERVAL '5 minutes';
```

### 5.4.4 Composite Indexes

Indexes với multiple columns để optimize complex queries:

```sql
-- Booking history with status filter
CREATE INDEX idx_bookings_user_status_created ON bookings(user_id, status, created_at DESC);

-- Show schedule by cinema
CREATE INDEX idx_shows_cinema_date ON shows(hall_id, show_date, show_time)
INCLUDE (movie_id, base_price);

-- Seat map query optimization
CREATE INDEX idx_show_seats_show_status_seat ON show_seats(show_id, status)
INCLUDE (seat_id, price);
```

### 5.4.5 Partial Indexes

Indexes chỉ trên subset của data:

```sql
-- Index only ACTIVE users
CREATE INDEX idx_users_active ON users(email)
WHERE status = 'ACTIVE';

-- Index only SCHEDULED shows
CREATE INDEX idx_shows_scheduled ON shows(show_date, show_time)
WHERE status = 'SCHEDULED';

-- Index only AVAILABLE seats
CREATE INDEX idx_show_seats_available ON show_seats(show_id)
WHERE status = 'AVAILABLE';

-- Index only PENDING bookings
CREATE INDEX idx_bookings_pending ON bookings(created_at)
WHERE status = 'PENDING';
```

### 5.4.6 Index Maintenance

**Monitor index usage:**

```sql
-- Check index usage statistics
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
ORDER BY idx_scan ASC;

-- Find unused indexes
SELECT
    schemaname,
    tablename,
    indexname
FROM pg_stat_user_indexes
WHERE idx_scan = 0
AND indexname NOT LIKE '%_pkey';
```

**Rebuild fragmented indexes:**

```sql
-- Reindex specific index
REINDEX INDEX idx_bookings_user_created;

-- Reindex entire table
REINDEX TABLE bookings;

-- Reindex concurrently (no lock)
REINDEX INDEX CONCURRENTLY idx_shows_lookup;
```

---

## 5.5 Redis Data Structures

Redis được sử dụng cho caching, session management, và distributed locking.

### 5.5.1 Distributed Locking (Seat Lock)

**Data structure:** String with TTL

**Key format:**
```
seat:lock:{show_id}:{seat_id}
```

**Value:** `user_id`

**TTL:** 300 seconds (5 minutes)

**Operations:**

```redis
# Lock a seat
SET seat:lock:501:A1 123 NX EX 300
# Returns: OK (if successful) or nil (if already locked)

# Check lock owner
GET seat:lock:501:A1
# Returns: "123" (user_id) or nil

# Release lock
DEL seat:lock:501:A1

# Check TTL
TTL seat:lock:501:A1
# Returns: remaining seconds or -2 (expired)

# Get all locked seats for a show
KEYS seat:lock:501:*
# Note: Use SCAN in production instead of KEYS
```

**Java implementation:**

```java
@Component
public class SeatLockService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean lockSeat(Long showId, String seatId, Long userId) {
        String key = String.format("seat:lock:%d:%s", showId, seatId);
        String value = userId.toString();

        Boolean success = redisTemplate.opsForValue()
            .setIfAbsent(key, value, Duration.ofSeconds(300));

        return Boolean.TRUE.equals(success);
    }

    public boolean isLockedByUser(Long showId, String seatId, Long userId) {
        String key = String.format("seat:lock:%d:%s", showId, seatId);
        String lockOwner = redisTemplate.opsForValue().get(key);

        return userId.toString().equals(lockOwner);
    }

    public void unlockSeat(Long showId, String seatId) {
        String key = String.format("seat:lock:%d:%s", showId, seatId);
        redisTemplate.delete(key);
    }

    public void unlockAllSeats(Long showId, List<String> seatIds) {
        List<String> keys = seatIds.stream()
            .map(seatId -> String.format("seat:lock:%d:%s", showId, seatId))
            .collect(Collectors.toList());

        redisTemplate.delete(keys);
    }
}
```

### 5.5.2 Session Management (JWT Token Blacklist)

**Data structure:** Set

**Key format:**
```
session:blacklist:{user_id}
```

**Value:** Set of revoked token IDs

**TTL:** Token expiry time

**Operations:**

```redis
# Add token to blacklist
SADD session:blacklist:123 "token_abc123"
EXPIRE session:blacklist:123 86400

# Check if token is blacklisted
SISMEMBER session:blacklist:123 "token_abc123"
# Returns: 1 (blacklisted) or 0 (valid)

# Remove all sessions for user (logout all devices)
DEL session:blacklist:123
```

### 5.5.3 Caching (Movie List, Show Seats)

**1. Cache movie list:**

**Key format:** `cache:movies:status:{status}`

**Value:** JSON string of movie list

**TTL:** 300 seconds (5 minutes)

```redis
# Set cache
SET cache:movies:status:NOW_SHOWING '[{"movieId":1,"title":"Avatar",...}]' EX 300

# Get cache
GET cache:movies:status:NOW_SHOWING

# Invalidate cache when movie added/updated
DEL cache:movies:status:NOW_SHOWING
```

**2. Cache show seat map:**

**Key format:** `cache:show:seats:{show_id}`

**Value:** JSON string of seat map

**TTL:** 60 seconds (1 minute) - Short TTL vì thay đổi thường xuyên

```redis
# Set cache
SET cache:show:seats:501 '{"showId":501,"seats":[...]}' EX 60

# Get cache
GET cache:show:seats:501

# Invalidate when seat booked
DEL cache:show:seats:501
```

**3. Cache user booking history:**

**Key format:** `cache:user:bookings:{user_id}`

**TTL:** 600 seconds (10 minutes)

```redis
SET cache:user:bookings:123 '[{...}]' EX 600
GET cache:user:bookings:123
DEL cache:user:bookings:123
```

**Java implementation:**

```java
@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public <T> void cache(String key, T value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return objectMapper.convertValue(value, type);
    }

    public void invalidate(String key) {
        redisTemplate.delete(key);
    }

    public void invalidatePattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // Cache show seats with automatic invalidation
    public SeatMap getCachedSeatMap(Long showId, Supplier<SeatMap> loader) {
        String key = String.format("cache:show:seats:%d", showId);

        SeatMap cached = get(key, SeatMap.class);
        if (cached != null) {
            return cached;
        }

        SeatMap loaded = loader.get();
        cache(key, loaded, Duration.ofSeconds(60));

        return loaded;
    }
}
```

### 5.5.4 Rate Limiting

**Data structure:** String (counter) with TTL

**Key format:** `rate:limit:{endpoint}:{user_id}:{minute}`

**Value:** Request count

**TTL:** 60 seconds

**Operations:**

```redis
# Increment request count
INCR rate:limit:/api/bookings:123:2025011610
EXPIRE rate:limit:/api/bookings:123:2025011610 60

# Check if limit exceeded
GET rate:limit:/api/bookings:123:2025011610
# If > 10, reject request
```

**Java implementation:**

```java
@Component
public class RateLimiter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean isAllowed(String endpoint, Long userId, int maxRequests) {
        String minute = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String key = String.format("rate:limit:%s:%d:%s", endpoint, userId, minute);

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(60));
        }

        return count <= maxRequests;
    }
}

// Usage in Filter
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Long userId = extractUserId(request);
        String endpoint = request.getRequestURI();

        if (!rateLimiter.isAllowed(endpoint, userId, 100)) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
```

### 5.5.5 Pub/Sub for Real-time Updates

**Channel format:** `channel:seat:update:{show_id}`

**Message format:** JSON

**Use case:** Notify clients when seat status changes

```redis
# Publisher (Backend)
PUBLISH channel:seat:update:501 '{"seatId":"A1","status":"SOLD"}'

# Subscriber (WebSocket server)
SUBSCRIBE channel:seat:update:501
```

**Java implementation:**

```java
@Service
public class SeatUpdatePublisher {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void publishSeatUpdate(Long showId, String seatId, String status) {
        String channel = String.format("channel:seat:update:%d", showId);

        Map<String, String> message = Map.of(
            "seatId", seatId,
            "status", status,
            "timestamp", Instant.now().toString()
        );

        redisTemplate.convertAndSend(channel, message);
    }
}

@Component
public class SeatUpdateSubscriber {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSeatUpdate(RedisMessageEvent event) {
        String channel = event.getChannel();
        Long showId = extractShowId(channel);

        // Forward to WebSocket clients
        messagingTemplate.convertAndSend(
            "/topic/show/" + showId + "/seats",
            event.getMessage()
        );
    }
}
```

### 5.5.6 Redis Configuration

**redis.conf settings:**

```conf
# Memory
maxmemory 2gb
maxmemory-policy allkeys-lru

# Persistence (AOF for durability)
appendonly yes
appendfsync everysec

# Replication (for HA)
replica-read-only yes

# Timeout
timeout 300

# Max clients
maxclients 10000
```

**Spring Boot configuration:**

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: ${REDIS_PASSWORD}
    database: 0
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: 1000ms
```

---

## 5.6 Kết luận chương

Chương 5 đã trình bày thiết kế chi tiết cơ sở dữ liệu cho hệ thống đặt vé rạp chiếu phim:

**1. ER Diagram và Relationships (5.1)**
- Xác định 11 entities chính: Users, Movies, Cinemas, Halls, Seats, Shows, ShowSeats, Bookings, BookingSeats, Genres
- Định nghĩa cardinality: 1:N (User-Booking, Show-Booking), N:M (Movie-Genre)
- Mô hình hóa business domain với các quan hệ rõ ràng

**2. Table Schema Design (5.2)**
- Thiết kế 11 bảng với DDL SQL đầy đủ
- Định nghĩa columns với kiểu dữ liệu phù hợp (BIGINT, VARCHAR, DECIMAL, TIMESTAMP)
- Primary keys sử dụng BIGSERIAL (auto-increment)
- Triggers tự động tạo show_seats khi tạo show mới
- Sample data cho testing

**3. Constraints và Business Rules (5.3)**
- **Foreign Keys:** ON DELETE CASCADE/RESTRICT, ON UPDATE CASCADE
- **Unique Constraints:** Email, username, seat positions
- **Check Constraints:** Validate giá trị (status, ratings, prices > 0)
- **Complex Triggers:**
  - Prevent double booking
  - Auto-calculate booking amounts
  - Update user points on booking confirmation/cancellation
  - Check show scheduling conflicts

**4. Indexing Strategy (5.4)**
- **Primary Key Indexes:** Tự động trên tất cả PKs
- **Foreign Key Indexes:** Tăng tốc JOINs
- **Full-text Search:** GIN index cho movie search
- **Composite Indexes:** Multi-column cho complex queries
- **Partial Indexes:** Index chỉ trên subset (ACTIVE users, PENDING bookings)
- Index maintenance: Monitor usage, rebuild fragmented indexes

**5. Redis Data Structures (5.5)**
- **Distributed Locking:** String với TTL cho seat locks
- **Session Management:** Set cho JWT blacklist
- **Caching:** Movie list, seat maps, user history
- **Rate Limiting:** Counter với TTL
- **Pub/Sub:** Real-time seat update notifications

**Key Design Decisions:**

1. **Normalization:** Tuân thủ 3NF, tách show_seats và booking_seats để tránh redundancy

2. **Referential Integrity:** Foreign keys đảm bảo data consistency

3. **Performance Optimization:**
   - Indexing chiến lược cho hot queries
   - Redis caching cho read-heavy operations
   - Partial indexes tiết kiệm storage

4. **Concurrency Control:**
   - Redis distributed locks với atomic operations
   - Database transactions với isolation level
   - Optimistic locking với version fields

5. **Scalability:**
   - BIGINT PKs (support 9 quintillion records)
   - Partitioning strategy (future: partition shows by date)
   - Read replicas cho reporting queries

6. **Data Integrity:**
   - Check constraints validate business rules
   - Triggers enforce complex constraints
   - NOT NULL cho required fields

**Database Size Estimates (10K users, 200K bookings/year):**

| Table | Rows | Size per row | Total Size |
|-------|------|--------------|------------|
| users | 10,000 | 500 bytes | 5 MB |
| movies | 200 | 2 KB | 400 KB |
| cinemas | 5 | 1 KB | 5 KB |
| halls | 30 | 1 KB | 30 KB |
| seats | 3,000 | 200 bytes | 600 KB |
| shows | 50,000/year | 500 bytes | 25 MB |
| show_seats | 150M (50K shows × 3K seats) | 100 bytes | 15 GB |
| bookings | 200,000/year | 1 KB | 200 MB |
| booking_seats | 600,000/year | 200 bytes | 120 MB |
| **Total** | | | **~15.5 GB/year** |

**Redis Memory Estimate:**

| Data Type | Items | Size per item | Total |
|-----------|-------|---------------|-------|
| Seat locks | 500 concurrent | 100 bytes | 50 KB |
| Cache (movies) | 1 item | 50 KB | 50 KB |
| Cache (seats) | 50 shows | 100 KB each | 5 MB |
| Rate limiting | 1000 users | 50 bytes | 50 KB |
| **Total** | | | **~6 MB** |

Thiết kế database đảm bảo:
- **Performance:** < 2s response time với proper indexing
- **Scalability:** Support 10K users hiện tại, có thể scale đến 100K users
- **Reliability:** ACID transactions, referential integrity
- **Maintainability:** Clear schema, documented constraints

Chương tiếp theo sẽ trình bày implementation và testing của hệ thống.

---

**Trang tiếp theo:** Chương 6 - Xây dựng mẫu thử
