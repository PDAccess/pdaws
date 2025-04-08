ALTER TABLE tenant
ADD deleted_at TIMESTAMP;

ALTER TABLE users
ADD deleted_at TIMESTAMP;

ALTER TABLE users
ADD external VARCHAR(50);