CREATE TABLE maintenance(
unique_id character varying(255) NOT NULL,
service_id character varying(255) NOT NULL,
user_id character varying(255) NOT NULL,
start_date TIMESTAMP,
end_date TIMESTAMP
);