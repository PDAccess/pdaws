CREATE TABLE exec_file_filters (
    id INT PRIMARY KEY,
    users text,
    paths text,
    service_id VARCHAR(50),
    group_id VARCHAR(50)
);