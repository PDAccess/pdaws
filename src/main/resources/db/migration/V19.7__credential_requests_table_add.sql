CREATE TABLE IF NOT EXISTS credential_requests (
    id INTEGER,
    credential_id VARCHAR(36),
    requesting_user VARCHAR(255),
    responding_user VARCHAR(255),
    is_approval BOOLEAN,
    requested_at TIMESTAMP,
    responded_at TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS credential_requests_table_sequence
    INCREMENT 1
    MINVALUE 1;
