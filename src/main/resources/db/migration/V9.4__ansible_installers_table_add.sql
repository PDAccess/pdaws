CREATE TABLE ansible_installers (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    become BOOLEAN,
    port INT,
    status BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
