CREATE TABLE IF NOT EXISTS job_histories (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    is_success BOOLEAN,
    started_at TIMESTAMP,
    finished_at TIMESTAMP
);

CREATE SEQUENCE job_histories_table_sequence
    INCREMENT 1
    MINVALUE 1;