ALTER TABLE groupuser
ADD expiredate TIMESTAMP,
ADD created_at TIMESTAMP,
Add who_create VARCHAR(50);

ALTER TABLE userservice
DROP COLUMN expiretime;