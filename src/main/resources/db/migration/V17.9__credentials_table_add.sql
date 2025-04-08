CREATE TABLE IF NOT EXISTS credentials (
    id VARCHAR(36),
    username VARCHAR(255),
    who_create VARCHAR(255),
    group_id VARCHAR(255),
    service_id VARCHAR(255),
    connection_user INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

alter table breaktheglass add column IF NOT EXISTS checkout_time TIMESTAMP;

alter table breaktheglass add column IF NOT EXISTS credential_id VARCHAR(36);
