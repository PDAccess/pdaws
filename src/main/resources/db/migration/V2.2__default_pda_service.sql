INSERT INTO tenant (tenant_id, company_name, country)
VALUES ('123','adminTenant','adminCountry');

INSERT INTO users (user_id, email, first_name, last_name, phone, remember_token, urole, username, external, tenant_id)
VALUES ('123','admin@mail','admin','admin','123','123','Admin','admin','internal','123');

INSERT INTO inventory (inventory_id, name, operating_system_id, service_type_id, who_create, who_update, description)
VALUES ('123','PDA','1','2','123','123','PDA Service');

