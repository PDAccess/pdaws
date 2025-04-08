CREATE TABLE template_users(
  id serial PRIMARY KEY,
  title varchar(255),
  username varchar(255),
  password varchar(255),
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted_at TIMESTAMP
);