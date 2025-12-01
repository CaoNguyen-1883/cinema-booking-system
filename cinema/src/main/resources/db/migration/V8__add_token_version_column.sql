-- Add token_version column to users table for JWT token revocation
-- This column is used to invalidate all tokens for a user when needed (logout, password change, etc.)

ALTER TABLE users ADD COLUMN IF NOT EXISTS token_version BIGINT NOT NULL DEFAULT 0;

-- Create index for token_version lookup
CREATE INDEX IF NOT EXISTS idx_users_token_version ON users(token_version);

COMMENT ON COLUMN users.token_version IS 'Token version for JWT revocation - increment to invalidate all existing tokens';
