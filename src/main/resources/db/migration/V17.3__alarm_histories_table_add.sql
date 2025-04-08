CREATE TABLE IF NOT EXISTS alarm_histories  (
        id BIGINT NOT NULL,
            alarm_id INTEGER NOT NULL,
                user_name character varying(255),
                    created_at TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS public.alarms_histories_table_sequence
    INCREMENT 1
        MINVALUE 1;
        
)
