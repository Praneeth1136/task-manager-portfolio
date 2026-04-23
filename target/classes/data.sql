-- Insert Users
-- Passwords are BCrypt hashed for 'pass123' -> $2a$10$7Z/U5bH29F2vjY8x8G0HruH7vCqL49J4JvH6u2d3FjB0lE0nS/UOW
INSERT IGNORE INTO users (id, name, email, password, created_at) VALUES 
(1, 'Praneeth Admin', 'praneeth@gmail.com', '$2a$10$7Z/U5bH29F2vjY8x8G0HruH7vCqL49J4JvH6u2d3FjB0lE0nS/UOW', NOW()),
(2, 'Test User', 'test@gmail.com', '$2a$10$7Z/U5bH29F2vjY8x8G0HruH7vCqL49J4JvH6u2d3FjB0lE0nS/UOW', NOW());

-- Insert Tasks for Praneeth
INSERT IGNORE INTO tasks (id, title, description, status, priority, due_date, created_at, updated_at, user_id) VALUES
(1, 'Setup Spring Boot Project', 'Initialize project with all dependencies', 0, 2, '2026-12-31', NOW(), NOW(), 1),
(2, 'Implement JWT Authentication', 'Add Spring Security and configure JWT', 1, 2, '2026-05-01', NOW(), NOW(), 1),
(3, 'Create Database Schema', 'Design models and repositories', 2, 1, '2026-04-20', NOW(), NOW(), 1);

-- Insert Tasks for Test User
INSERT IGNORE INTO tasks (id, title, description, status, priority, due_date, created_at, updated_at, user_id) VALUES
(4, 'Write Unit Tests', 'Test controllers and services', 0, 1, '2026-06-15', NOW(), NOW(), 2),
(5, 'Update README files', 'Write excellent documentation', 0, 0, '2026-07-10', NOW(), NOW(), 2);
