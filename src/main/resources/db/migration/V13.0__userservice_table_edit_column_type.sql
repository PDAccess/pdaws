ALTER TABLE userservice
ALTER COLUMN connection_user TYPE INT
USING connection_user::integer;