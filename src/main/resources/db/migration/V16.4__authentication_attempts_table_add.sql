CREATE TABLE IF NOT EXISTS authentication_attempts (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255),
    user_agent VARCHAR(255),
    ip_address VARCHAR(255),
    reason VARCHAR(255),
    login_type VARCHAR(255),
    is_success BOOLEAN,
    attempted_at TIMESTAMP
);

CREATE SEQUENCE authentication_attempts_table_sequence
    INCREMENT 1
    MINVALUE 1;