ALTER TABLE policy
DROP COLUMN id;

ALTER TABLE policy
ADD id varchar(255);

ALTER TABLE users
DROP COLUMN company;

