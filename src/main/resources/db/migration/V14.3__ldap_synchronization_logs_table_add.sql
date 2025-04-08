CREATE TABLE IF NOT EXISTS ldap_synchronization_logs (
id INT PRIMARY KEY,
group_id VARCHAR(255),
created_users TEXT,
added_users TEXT,
deleted_users TEXT,
created_at TIMESTAMP,
updated_at TIMESTAMP
);