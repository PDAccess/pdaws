CREATE SEQUENCE psessions_table_sequence
    INCREMENT 1
    MINVALUE 1;

ALTER TABLE psessions
ALTER COLUMN session_id TYPE INT
USING session_id::integer;