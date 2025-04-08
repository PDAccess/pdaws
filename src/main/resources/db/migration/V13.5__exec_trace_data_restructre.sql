DROP TABLE IF EXISTS exec_trace_data;

CREATE TABLE IF NOT EXISTS exec_trace_data
(
id INTEGER,
host VARCHAR(255),
user_name VARCHAR(255),
user_id VARCHAR(255),
command VARCHAR(255),
params VARCHAR(255),
exec_time VARCHAR(255),
groupid VARCHAR(255),
serviceid VARCHAR(255),
report_time TIMESTAMP
) partition by range (report_time);

create index if not exists exec_trace_data_id on exec_trace_data(id);
CREATE INDEX if not exists exec_trace_data_command ON exec_trace_data ((lower(command)));
CREATE INDEX if not exists exec_trace_data_serviceid ON exec_trace_data (serviceid);
CREATE INDEX if not exists exec_trace_data_groupid ON exec_trace_data (groupid);
CREATE INDEX if not exists exec_trace_data_exec_time ON exec_trace_data (exec_time);