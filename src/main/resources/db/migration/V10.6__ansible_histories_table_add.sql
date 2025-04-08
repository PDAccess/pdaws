CREATE TABLE ansible_histories (
    id INT PRIMARY KEY,
    installer_id INT,
    finished BOOLEAN,
    success BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE SEQUENCE ansible_histories_table_sequence
    INCREMENT 1
    MINVALUE 1;