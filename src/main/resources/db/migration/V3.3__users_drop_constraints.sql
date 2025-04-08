ALTER TABLE users alter column phone drop not null;
ALTER TABLE users alter column first_name drop not null;
ALTER TABLE users alter column last_name drop not null;
ALTER TABLE users alter column email drop not null;
ALTER TABLE users DROP CONSTRAINT users_email_unique;