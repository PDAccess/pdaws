CREATE TABLE IF NOT EXISTS notifications (
    id VARCHAR(36),
    upper_id VARCHAR(36),
    notify_type INTEGER,
    who_create VARCHAR(36),
    active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);