CREATE TABLE exec_trace_filters (
    id INT PRIMARY KEY,
    users text,
    regexes text,
    service_id VARCHAR(50),
    group_id VARCHAR(50)
);