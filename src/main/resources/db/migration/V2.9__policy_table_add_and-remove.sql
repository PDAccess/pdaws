
ALTER TABLE policy
ADD behavior VARCHAR(50),
ADD service_type VARCHAR(50),
ADD service_meta_type VARCHAR(50),
ADD operating_system VARCHAR(50);

ALTER TABLE policy
DROP COLUMN status,
DROP COLUMN updated_at,
DROP COLUMN inventoryid,
DROP COLUMN regex;


ALTER TABLE userservice
ADD policyid VARCHAR(50),
ADD expiredate TIMESTAMP;

CREATE TABLE policyregex
(policyid VARCHAR(50),
regex VARCHAR(50));