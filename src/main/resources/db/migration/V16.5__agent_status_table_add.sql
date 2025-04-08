CREATE TABLE IF NOT EXISTS agent_status (
    id INT PRIMARY KEY,
    service_id VARCHAR(255),
    group_id VARCHAR(255),
    hostname VARCHAR(255),
    ip_address VARCHAR(255),
    statistics_data TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE SEQUENCE agent_status_table_sequence
    INCREMENT 1
    MINVALUE 1;