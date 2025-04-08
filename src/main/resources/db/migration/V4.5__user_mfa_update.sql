alter table users drop COLUMN twofactorauth;
alter table users add COLUMN mfa BOOLEAN DEFAULT FALSE;