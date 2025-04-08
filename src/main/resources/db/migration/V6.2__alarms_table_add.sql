CREATE TABLE alarms
    (id integer,
    name VARCHAR(50),
    description VARCHAR(255),
    message VARCHAR(255),
    group_id VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN)