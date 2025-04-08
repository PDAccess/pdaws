ALTER TABLE plogs
ALTER COLUMN session_id TYPE INT
USING session_id::integer;