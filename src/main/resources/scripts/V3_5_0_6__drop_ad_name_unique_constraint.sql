ALTER TABLE users DROP CONSTRAINT users_ad_name_key;
DROP INDEX IF EXISTS users_ad_name_index;
DROP INDEX IF EXISTS users_ad_name_key;