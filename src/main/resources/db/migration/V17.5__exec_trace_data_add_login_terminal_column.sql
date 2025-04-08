ALTER TABLE exec_trace_data
ADD COLUMN IF NOT EXISTS login_terminal VARCHAR(50);
