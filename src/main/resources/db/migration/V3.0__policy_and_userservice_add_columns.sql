ALTER TABLE policy
ADD name VARCHAR(50);

ALTER TABLE userservice
ADD who_create VARCHAR(50),
ADD expiretime TIMESTAMP,
ADD created_at TIMESTAMP;