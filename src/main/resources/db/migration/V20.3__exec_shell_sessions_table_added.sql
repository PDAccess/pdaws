CREATE TABLE IF NOT EXISTS exec_shell_sessions  (
        id BIGINT,
        session_id VARCHAR(50),
        client_ip VARCHAR(50),
        start_time TIMESTAMP,
        end_time TIMESTAMP,
        username VARCHAR(50),
        user_id INTEGER,
        e_username VARCHAR(50),
        e_user_id INTEGER,
        service_id VARCHAR(255)
);

CREATE SEQUENCE IF NOT EXISTS public.exec_shell_sessions_table_sequence
        INCREMENT 1
        MINVALUE 1;
);
