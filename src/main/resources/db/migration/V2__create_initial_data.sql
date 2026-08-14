-- 1. Password: "password123"
INSERT INTO users (id, name, email, password, role, created_at)
VALUES (1, 'Billy Jerson', 'billy@example.com', '$2a$12$O17kdLYuYYCKRSCYufeT6uXv0Y9b6c9nzperkQlMaAMOtY5JY2vyq', 'ROLE_CLIENT', NOW())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, name, email, password, role, created_at)
VALUES (2, 'Dr. Miguel Torres', 'proveedor@example.com', '$2a$12$O17kdLYuYYCKRSCYufeT6uXv0Y9b6c9nzperkQlMaAMOtY5JY2vyq', 'ROLE_PROVIDER', NOW())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, name, email, password, role, created_at)
VALUES (3, 'Admin Sistema', 'admin@example.com', '$2a$12$O17kdLYuYYCKRSCYufeT6uXv0Y9b6c9nzperkQlMaAMOtY5JY2vyq', 'ROLE_ADMIN', NOW())
    ON CONFLICT (id) DO NOTHING;

-- 2. Insert services for provider (ID: 2)
-- Duration: 60 min y 30 min
INSERT INTO services (id, name, description, duration_minutes, price, provider_id)
VALUES (1, 'Consulta Especializada', 'Atención médica y evaluación completa', 60, 150.00, 2)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO services (id, name, description, duration_minutes, price, provider_id)
VALUES (2, 'Revisión Rápida', 'Control general de rutina', 30, 80.00, 2)
    ON CONFLICT (id) DO NOTHING;

-- 3. Insert availabilities for provider (ID: 2)
-- Works Mondays and Wednesdays from 08:00 to 18:00
INSERT INTO availabilities (id, day_of_week, start_time, end_time, provider_id)
VALUES (1, 'MONDAY', '08:00:00', '18:00:00', 2)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO availabilities (id, day_of_week, start_time, end_time, provider_id)
VALUES (2, 'WEDNESDAY', '08:00:00', '18:00:00', 2)
    ON CONFLICT (id) DO NOTHING;

-- Adjust PostgreSQL sequences to avoid conflicts with future records created by JPA.
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE(MAX(id), 1)) FROM users;
SELECT setval(pg_get_serial_sequence('services', 'id'), COALESCE(MAX(id), 1)) FROM services;
SELECT setval(pg_get_serial_sequence('availabilities', 'id'), COALESCE(MAX(id), 1)) FROM availabilities;