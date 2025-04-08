CREATE OR REPLACE FUNCTION exec_trace_short(record_id BIGINT)
RETURNS table (
        		id BIGINT,
                host VARCHAR,
                user_name VARCHAR,
                user_id VARCHAR,
                command VARCHAR,
                params VARCHAR,
                exec_time VARCHAR,
                groupid VARCHAR,
                serviceid VARCHAR,
                login_user VARCHAR,
                login_address VARCHAR,
                login_time TIMESTAMP
        	)

AS $$
 select id, host, user_name, user_id, command, params, exec_time, groupid, serviceid, login_user, login_address, login_time
 		from exec_trace_data
 		where  ( report_time < now() and report_time > (now() - INTERVAL '1 DAY' )) and (id > record_id and  id < record_id + 10000);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION exec_trace_long(record_id BIGINT)
RETURNS table (
        		id BIGINT,
                host VARCHAR,
                user_name VARCHAR,
                user_id VARCHAR,
                command VARCHAR,
                params VARCHAR,
                exec_time VARCHAR,
                groupid VARCHAR,
                serviceid VARCHAR,
                login_user VARCHAR,
                login_address VARCHAR,
                login_time TIMESTAMP
        	)

AS $$
 select id, host, user_name, user_id, command, params, exec_time, groupid, serviceid, login_user, login_address, login_time
 		from exec_trace_data
 		where  ( report_time < now() and report_time > (now() - INTERVAL '7 DAY' )) and (id > record_id and  id < record_id + 10000);
$$ LANGUAGE SQL;