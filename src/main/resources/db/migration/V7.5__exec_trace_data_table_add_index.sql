ALTER TABLE ONLY exec_trace_data
    ADD CONSTRAINT exec_trace_data_pkey PRIMARY KEY (id);

CREATE INDEX ON exec_trace_data ((lower(command)));
CREATE INDEX ON exec_trace_data (serviceid);
CREATE INDEX ON exec_trace_data (groupid);
CREATE INDEX ON exec_trace_data (exec_time);