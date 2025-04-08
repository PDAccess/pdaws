CREATE TABLE connection_users (
    id INT PRIMARY KEY,
    username VARCHAR(50),
    service_id VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE SEQUENCE connection_users_table_sequence
    INCREMENT 1
    MINVALUE 1;