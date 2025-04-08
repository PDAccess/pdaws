ALTER TABLE IF EXISTS inventory DROP CONSTRAINT IF EXISTS inventory_who_create_foreign;
ALTER TABLE IF EXISTS inventory DROP CONSTRAINT IF EXISTS inventory_who_update_foreign;
ALTER TABLE IF EXISTS plogs DROP CONSTRAINT IF EXISTS plogs_session_id_foreign;
ALTER TABLE IF EXISTS policy DROP CONSTRAINT IF EXISTS policy_who_create_foreign;
ALTER TABLE IF EXISTS psessions DROP CONSTRAINT IF EXISTS psessions_inventory_id_foreign;
ALTER TABLE IF EXISTS users DROP CONSTRAINT IF EXISTS users_tenant_id_foreign;