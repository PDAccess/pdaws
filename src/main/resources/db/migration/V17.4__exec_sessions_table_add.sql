CREATE TABLE IF NOT EXISTS exec_sessions  (
        session_id BIGINT,
            login_terminal VARCHAR(50),
                session_start_time TIMESTAMP,
                    session_end_time TIMESTAMP,
                        remote_address VARCHAR(50),
                            login_user VARCHAR(50),
                                service_id VARCHAR(255)
                                );

                                CREATE SEQUENCE IF NOT EXISTS public.exec_sessions_table_sequence
                                    INCREMENT 1
                                        MINVALUE 1;
                                        
)
