CREATE TABLE IF NOT EXISTS group_properties (
id INT PRIMARY KEY,
group_id VARCHAR(255),
key VARCHAR(255),
value VARCHAR(255)
);

create sequence IF NOT EXISTS group_properties_table_sequence;