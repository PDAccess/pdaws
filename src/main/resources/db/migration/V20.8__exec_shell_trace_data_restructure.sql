DROP TABLE IF EXISTS exec_shell_trace_data;

CREATE TABLE IF NOT EXISTS exec_shell_trace_data  (
        id BIGINT,
        session_id VARCHAR(50),
        exec_time BIGINT,
        report_time TIMESTAMP,
        std_out TEXT,
        exec_command VARCHAR(255),
        service_id VARCHAR(255)
) partition by range (report_time);

create index if not exists exec_shell_trace_data_id on exec_shell_trace_data(id);
CREATE INDEX if not exists exec_shell_trace_data_exec_command ON exec_shell_trace_data((lower(exec_command)));
CREATE INDEX if not exists exec_shell_trace_data_service_id ON exec_shell_trace_data(service_id);
CREATE INDEX if not exists exec_shell_trace_data_exec_time ON exec_shell_trace_data(exec_time);
CREATE INDEX if not exists exec_shell_trace_data_report_time ON exec_shell_trace_data(report_time);