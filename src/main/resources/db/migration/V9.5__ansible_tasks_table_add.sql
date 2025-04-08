CREATE TABLE ansible_tasks (
    id INT PRIMARY KEY,
    installer_id INT,
    name VARCHAR(255),
    command VARCHAR(500),
    register VARCHAR(255),
    failed_when VARCHAR(255)
);
