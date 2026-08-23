CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC')
);

CREATE TABLE services (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          description TEXT,
                          duration_minutes INT NOT NULL,
                          price NUMERIC(10, 2) NOT NULL,
                          provider_id BIGINT NOT NULL,
                          CONSTRAINT fk_service_provider FOREIGN KEY (provider_id) REFERENCES users(id)
);

CREATE TABLE availabilities (
                                id BIGSERIAL PRIMARY KEY,
                                day_of_week VARCHAR(15) NOT NULL,
                                start_time TIME NOT NULL,
                                end_time TIME NOT NULL,
                                provider_id BIGINT NOT NULL,
                                CONSTRAINT fk_availability_provider FOREIGN KEY (provider_id) REFERENCES users(id)
);

CREATE TABLE appointments (
                              id BIGSERIAL PRIMARY KEY,
                              start_date_time TIMESTAMPTZ NOT NULL,
                              end_date_time TIMESTAMPTZ NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              client_id BIGINT NOT NULL,
                              service_id BIGINT NOT NULL,
                              created_at TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
                              CONSTRAINT fk_appointment_client FOREIGN KEY (client_id) REFERENCES users(id),
                              CONSTRAINT fk_appointment_service FOREIGN KEY (service_id) REFERENCES services(id)
);

CREATE TABLE outbox_emails (
                               id BIGSERIAL PRIMARY KEY,
                               recipient VARCHAR(255) NOT NULL,
                               subject VARCHAR(255) NOT NULL,
                               content TEXT NOT NULL,
                               status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                               retry_count INT NOT NULL DEFAULT 0,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
                               processed_at TIMESTAMPTZ,
                               error_message TEXT
);

CREATE INDEX idx_outbox_emails_status ON outbox_emails(status);
CREATE INDEX idx_outbox_emails_created_at ON outbox_emails(created_at);