ALTER TABLE IF EXISTS inventory
    DROP CONSTRAINT IF EXISTS inventory_service_type_id_foreign;

DROP TABLE IF EXISTS service_types;

DROP SEQUENCE IF EXISTS service_types_id_seq;