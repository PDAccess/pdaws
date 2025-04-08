CREATE TABLE exec_auth_data (
    id INT PRIMARY KEY,
    host VARCHAR(255),
    user_name VARCHAR(255),
    user_id VARCHAR(255),
    tty VARCHAR(255),
    status VARCHAR(255),
    service_id VARCHAR(50),
    group_id VARCHAR(50),
    exec_time TIMESTAMP,
    report_time TIMESTAMP
);