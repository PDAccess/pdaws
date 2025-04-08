ALTER TABLE inventory
ADD COLUMN IF NOT EXISTS operating_system_version VARCHAR(50);
