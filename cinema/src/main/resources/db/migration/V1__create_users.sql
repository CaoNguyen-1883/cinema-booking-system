-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    avatar_url VARCHAR(500),
    points INTEGER NOT NULL DEFAULT 0,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
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

COMMENT ON TABLE users IS 'User accounts table';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';
COMMENT ON COLUMN users.points IS 'Loyalty points (1000 VND = 1 point)';
COMMENT ON COLUMN users.role IS 'User role: CUSTOMER, STAFF, ADMIN';
COMMENT ON COLUMN users.status IS 'Account status: ACTIVE, INACTIVE, BANNED';
