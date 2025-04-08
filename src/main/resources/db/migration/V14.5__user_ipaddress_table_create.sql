create table user_ip_addresses
(
    id serial primary key,
    user_id varchar(255) ,
    ip_address varchar(16) not null
);