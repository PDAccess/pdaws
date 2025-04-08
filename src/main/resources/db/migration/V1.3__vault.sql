--
-- Vault table and functions
--

CREATE TABLE IF NOT EXISTS vault_kv_store (
    parent_path text COLLATE pg_catalog."C" NOT NULL,
    path text COLLATE pg_catalog."C" NOT NULL,
    key text COLLATE pg_catalog."C" NOT NULL,
    value bytea
);

ALTER TABLE ONLY vault_kv_store DROP CONSTRAINT IF EXISTS pkey;
ALTER TABLE ONLY vault_kv_store ADD CONSTRAINT pkey PRIMARY KEY (path, key);

CREATE INDEX IF NOT EXISTS parent_path_idx ON vault_kv_store USING btree (parent_path);

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;

CREATE OR REPLACE FUNCTION vault_kv_put(_parent_path text, _path text, _key text, _value bytea) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    LOOP
        -- first try to update the key
        UPDATE vault_kv_store
          SET (parent_path, path, key, value) = (_parent_path, _path, _key, _value)
          WHERE _path = path AND key = _key;
        IF found THEN
            RETURN;
        END IF;
        -- not there, so try to insert the key
        -- if someone else inserts the same key concurrently,
        -- we could get a unique-key failure
        BEGIN
            INSERT INTO vault_kv_store (parent_path, path, key, value)
              VALUES (_parent_path, _path, _key, _value);
            RETURN;
        EXCEPTION WHEN unique_violation THEN
            -- Do nothing, and loop to try the UPDATE again.
        END;
    END LOOP;
END;
$$;


--ALTER FUNCTION public.vault_kv_put(_parent_path text, _path text, _key text, _value bytea) OWNER TO pda;