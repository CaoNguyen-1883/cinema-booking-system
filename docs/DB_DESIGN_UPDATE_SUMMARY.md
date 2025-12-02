# Database Design Document Update Summary

This document summarizes all changes made to `05_THIET_KE_CO_SO_DU_LIEU.md` to match the actual code implementation.

## Changes Made

### 1. Users Table (Section 5.2.1)
- ✅ Changed `user_id BIGSERIAL PRIMARY KEY` → `id BIGSERIAL PRIMARY KEY`
- ✅ Added `avatar_url VARCHAR(500)` field after phone_number
- ✅ Added `token_version BIGINT NOT NULL DEFAULT 0` field after points
- ✅ Removed `CONSTRAINT users_email_format CHECK` constraint
- ✅ Added comments for avatar_url and token_version

### 2. Movies Table (Section 5.2.2)
- ✅ Changed `movie_id BIGSERIAL PRIMARY KEY` → `id BIGSERIAL PRIMARY KEY`
- ✅ Changed `cast TEXT` → `cast_members TEXT`
- ✅ Removed `genre VARCHAR(255) NOT NULL` (now uses movie_genres junction table)
- ✅ Added `end_date DATE` after release_date
- ✅ Added `banner_url VARCHAR(500)` after poster_url
- ✅ Changed language default from `'English'` → `'Vietnamese'`
- ✅ Added comments for cast_members, banner_url, and end_date

### 3. Cinemas Table (Section 5.2.3)
- ✅ Changed `cinema_id BIGSERIAL PRIMARY KEY` → `id BIGSERIAL PRIMARY KEY`
- ✅ Changed opening_hours default from `'06:00-24:00'` → `'08:00-24:00'`

### 4. Halls Table (Section 5.2.4)
- ✅ Changed `hall_id BIGSERIAL PRIMARY KEY` → `id BIGSERIAL PRIMARY KEY`
- ✅ Removed `seat_layout JSON` column
- ✅ Removed `screen_type VARCHAR(50)` column
- ✅ Removed `sound_system VARCHAR(50)` column
- ✅ Added `total_rows INTEGER NOT NULL` field
- ✅ Added `seats_per_row INTEGER NOT NULL` field
- ✅ Updated hall_type valid values: `'3D'` → `'THREE_D'`, `'4DX'` → `'FOUR_DX'`
- ✅ Updated comments to reflect new columns

### 5. Seats Table (Section 5.2.5)
- ✅ Changed `seat_id BIGSERIAL PRIMARY KEY` → `id BIGSERIAL PRIMARY KEY`
- ✅ Changed `row_number VARCHAR(5)` → `row_name VARCHAR(5)`
- ✅ Removed `position_x INTEGER` column
- ✅ Removed `position_y INTEGER` column
- ✅ Updated seat_type valid values: removed `'PREMIUM'`, kept only `'NORMAL', 'VIP', 'COUPLE'`
- ✅ Updated UNIQUE constraint to use row_name instead of row_number
- ✅ Updated comments for row_name and seat_type

### 6. Shows Table (Section 5.2.6)
- ✅ Changed `show_id BIGSERIAL PRIMARY KEY` → `id BIGSERIAL PRIMARY KEY`
- ✅ Changed `show_time TIME NOT NULL` → `start_time TIME NOT NULL`
- ✅ Added `end_time TIME NOT NULL` after start_time
- ✅ Added comments for start_time and end_time

### 7. Show_Seats Table (Section 5.2.7)
- ✅ Changed `show_seat_id BIGSERIAL PRIMARY KEY` → `id BIGSERIAL PRIMARY KEY`

### 8. Bookings Table (Section 5.2.8)
- ✅ Changed `booking_id BIGSERIAL PRIMARY KEY` → `id BIGSERIAL PRIMARY KEY`
- ✅ Added `booking_code VARCHAR(20) NOT NULL UNIQUE` as second column after id
- ✅ Added `expires_at TIMESTAMP` after qr_code
- ✅ Changed `paid_at TIMESTAMP` → `confirmed_at TIMESTAMP`
- ✅ Removed `payment_method VARCHAR(50)` (moved to Payments)
- ✅ Removed `transaction_id VARCHAR(255)` (moved to Payments)
- ✅ Removed `refund_id VARCHAR(255)` (moved to Payments)
- ✅ Removed `refunded_at TIMESTAMP` (handled in Payments)
- ✅ Removed `notes TEXT`
- ✅ Updated status valid values: `'FAILED'` → `'EXPIRED'`, removed `'REFUNDED'`
- ✅ Updated constraint to use confirmed_at instead of paid_at
- ✅ Added comments for booking_code and expires_at

### 9. Booking_Seats Table (Section 5.2.9)
- ✅ Changed `booking_seat_id BIGSERIAL PRIMARY KEY` → `id BIGSERIAL PRIMARY KEY`

### 10. Genres Table (Section 5.2.10)
- ✅ Changed `genre_id SERIAL PRIMARY KEY` → `id SERIAL PRIMARY KEY`

### 11. Payments Table (Section 5.2.12) - Major Simplification
- ✅ Changed `payment_id BIGSERIAL PRIMARY KEY` → `id BIGSERIAL PRIMARY KEY`
- ✅ Removed `payment_provider VARCHAR(50) NOT NULL` column
- ✅ Removed `currency VARCHAR(3) NOT NULL DEFAULT 'VND'` column
- ✅ Removed `provider_transaction_id VARCHAR(255)` column
- ✅ Removed `request_data JSONB` column
- ✅ Removed `response_data JSONB` column
- ✅ Removed `expired_at TIMESTAMP` column
- ✅ Removed `refund_id VARCHAR(255)` column
- ✅ Removed `refund_amount DECIMAL(10, 2)` column
- ✅ Removed `error_code VARCHAR(50)` column
- ✅ Removed `error_message TEXT` column
- ✅ Changed `callback_data JSONB` → `callback_data TEXT`
- ✅ Added `transaction_id VARCHAR(255)` field
- ✅ Added `paid_at TIMESTAMP` field
- ✅ Added `failed_at TIMESTAMP` field
- ✅ Added `failure_reason TEXT` field
- ✅ Updated payment_method valid values: removed `'CARD', 'BANK_TRANSFER'`, kept `'VNPAY', 'MOMO', 'ZALOPAY', 'CASH'`
- ✅ Updated status valid values: removed `'CANCELLED', 'EXPIRED'`, kept `'PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED'`
- ✅ Removed payment_provider constraint
- ✅ Updated comments to reflect simplified schema

## Global Changes

### Foreign Key References
- ✅ Updated all foreign key REFERENCES to use `id` instead of table-specific IDs:
  - `REFERENCES users(user_id)` → `REFERENCES users(id)`
  - `REFERENCES movies(movie_id)` → `REFERENCES movies(id)`
  - `REFERENCES cinemas(cinema_id)` → `REFERENCES cinemas(id)`
  - `REFERENCES halls(hall_id)` → `REFERENCES halls(id)`
  - `REFERENCES seats(seat_id)` → `REFERENCES seats(id)`
  - `REFERENCES shows(show_id)` → `REFERENCES shows(id)`
  - `REFERENCES show_seats(show_seat_id)` → `REFERENCES show_seats(id)`
  - `REFERENCES bookings(booking_id)` → `REFERENCES bookings(id)`
  - `REFERENCES genres(genre_id)` → `REFERENCES genres(id)`

### Relationship Descriptions (Section 5.1.2)
- ✅ Updated all relationship documentation to reflect new primary key names
- ✅ Examples:
  - `users.user_id` → `users.id`
  - `movies.movie_id` → `movies.id`
  - etc.

### Foreign Key Constraints (Section 5.3.1)
- ✅ Updated all ALTER TABLE ... ADD CONSTRAINT statements to reference `id` columns

### Primary Key Indexes (Section 5.4.1)
- ✅ Updated all primary key index documentation
- ✅ Examples:
  - `CREATE UNIQUE INDEX users_pkey ON users(user_id)` → `CREATE UNIQUE INDEX users_pkey ON users(id)`

### Trigger Functions
- ✅ Updated trigger function references to use new column names
- ✅ Updated `update_booking_on_payment()` to use `confirmed_at` instead of `paid_at`
- ✅ Updated `update_booking_on_payment()` to set status to `'EXPIRED'` instead of `'FAILED'`

### Sample Data and Indexes
- ✅ Removed index on `provider_transaction_id` in Payments table
- ✅ Updated index on `paid_at` → `confirmed_at` in Bookings table

## Summary Statistics

- **Total tables updated:** 11
- **Primary key columns renamed:** 11 (all from `{table}_id` to `id`)
- **New columns added:** 9
  - Users: avatar_url, token_version
  - Movies: banner_url, end_date
  - Halls: total_rows, seats_per_row
  - Bookings: booking_code, expires_at
  - Payments: failed_at, failure_reason
- **Columns removed:** 18
  - Movies: genre
  - Halls: seat_layout, screen_type, sound_system
  - Seats: position_x, position_y
  - Bookings: payment_method, transaction_id, refund_id, refunded_at, notes
  - Payments: payment_provider, currency, provider_transaction_id, request_data, response_data, expired_at, error_code, error_message
- **Columns renamed:** 3
  - Movies: cast → cast_members
  - Seats: row_number → row_name
  - Shows: show_time → start_time
  - Bookings: paid_at → confirmed_at
- **Enum value updates:** 3
  - Halls: hall_type (3D → THREE_D, 4DX → FOUR_DX)
  - Seats: seat_type (removed PREMIUM)
  - Bookings: status (FAILED → EXPIRED, removed REFUNDED)

## Verification

All changes have been successfully applied to the document. The database design now accurately reflects the actual Java entity implementations in the codebase.

Last updated: 2025-12-02
