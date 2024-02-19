ALTER TABLE users ADD COLUMN account_name VARCHAR(63);

UPDATE users SET account_name = SUBSTRING(mail FROM 1 FOR POSITION('@' IN mail) - 1);

ALTER TABLE users ALTER COLUMN account_name SET NOT NULL;

CREATE UNIQUE INDEX users_account_name_index ON users (account_name);