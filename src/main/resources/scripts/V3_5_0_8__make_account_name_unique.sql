ALTER TABLE users
ADD CONSTRAINT users_account_name_unique UNIQUE (account_name);