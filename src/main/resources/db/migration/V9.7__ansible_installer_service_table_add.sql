CREATE TABLE ansible_installer_service (
    installer_id INT,
    service_id VARCHAR(255),
    PRIMARY KEY (installer_id, service_id)
);
