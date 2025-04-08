ALTER TABLE inventory DROP auto_credantial_time;
ALTER TABLE inventory DROP auto_credantial_time_type;
CREATE TABLE auto_credantial_settings (
    credantial_id VARCHAR(255) PRIMARY KEY,
    inventory_id VARCHAR(255),
    auto_credantial_time INTEGER,
    auto_credantial_time_type VARCHAR(10),
    is_active BOOLEAN,
    created_at TIMESTAMP
);

CREATE TABLE auto_credantial_history (
    id VARCHAR(255) PRIMARY KEY,
    credantial_id VARCHAR(255),
    old_password VARCHAR(255),
    new_password VARCHAR(255),
    start_at TIMESTAMP,
    end_at TIMESTAMP,
    result VARCHAR(255),
    description TEXT
);