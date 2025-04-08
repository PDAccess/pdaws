ALTER TABLE IF EXISTS inventory
    DROP CONSTRAINT IF EXISTS inventory_operating_system_id_foreign;

DROP TABLE IF EXISTS operating_systems;

DROP SEQUENCE IF EXISTS operating_systems_id_seq;