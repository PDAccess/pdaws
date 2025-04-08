ALTER TABLE exec_trace_data ADD IF NOT EXISTS exec_timestamp TIMESTAMP;
ALTER TABLE exec_trace_data ADD IF NOT EXISTS hostname VARCHAR(255);