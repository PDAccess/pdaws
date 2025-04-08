CREATE TABLE snippets
(user_id VARCHAR(300),
snippet_id VARCHAR(300),
description VARCHAR(300),
title VARCHAR(300),
info VARCHAR(500),
operating_system_id integer NOT NULL,
service_type_id integer NOT NULL,
deleted_at TIMESTAMP,
created_at TIMESTAMP,
updated_at TIMESTAMP);