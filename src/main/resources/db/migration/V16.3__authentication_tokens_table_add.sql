CREATE TABLE IF NOT EXISTS authentication_tokens (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255),
    token VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE SEQUENCE authentication_tokens_table_sequence
    INCREMENT 1
    MINVALUE 1;