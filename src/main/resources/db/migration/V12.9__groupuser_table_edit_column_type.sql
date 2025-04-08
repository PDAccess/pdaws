ALTER TABLE groupuser
ALTER COLUMN connection_user TYPE INT
USING connection_user::integer;