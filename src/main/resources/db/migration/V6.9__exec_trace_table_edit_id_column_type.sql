ALTER TABLE exec_trace_data
    ALTER COLUMN id TYPE INT
    USING id::integer;