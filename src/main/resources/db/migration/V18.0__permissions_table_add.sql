CREATE TABLE IF NOT EXISTS permissions (
    permission_id INTEGER,
    who_create VARCHAR(36),
    user_id VARCHAR(36),
    credential_id VARCHAR(36),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS permissions_collection (
permission_id INTEGER,
collection_id INTEGER
);

CREATE SEQUENCE IF NOT EXISTS permissions_table_sequence
    INCREMENT 1
    MINVALUE 1;