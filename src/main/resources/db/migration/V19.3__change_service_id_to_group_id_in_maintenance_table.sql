ALTER TABLE maintenance DROP COLUMN IF EXISTS service_id;

ALTER TABLE maintenance ADD IF NOT EXISTS group_id VARCHAR(50);