CREATE TABLE IF NOT EXISTS exec_shell_trace_data  (
        id BIGINT,
        session_id VARCHAR(50),
        exec_time TIMESTAMP,
        std_out TEXT,
        exec_command VARCHAR(255),
        service_id VARCHAR(255)
);

CREATE SEQUENCE IF NOT EXISTS public.exec_shell_trace_data_table_sequence
        INCREMENT 1
        MINVALUE 1;
);
