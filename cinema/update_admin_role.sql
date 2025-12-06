-- Update user cineadmin to ADMIN role
UPDATE users SET role = 'ADMIN' WHERE id = 13;

-- Verify the update
SELECT id, username, email, full_name, role, status FROM users WHERE id = 13;
