CREATE TABLE exec_file_data (
    id INT PRIMARY KEY,
    host VARCHAR(255),
    user_name VARCHAR(255),
    user_id VARCHAR(255),
    path VARCHAR(255),
    file_name VARCHAR(255),
    file_action VARCHAR(255),
    service_id VARCHAR(50),
    group_id VARCHAR(50),
    exec_time TIMESTAMP,
    report_time TIMESTAMP
);