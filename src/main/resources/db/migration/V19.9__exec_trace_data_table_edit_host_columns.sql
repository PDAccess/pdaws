ALTER TABLE exec_trace_data ADD IF NOT EXISTS client_hostname VARCHAR(255);
ALTER TABLE exec_trace_data RENAME COLUMN hostname TO server_hostname;