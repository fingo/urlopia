CREATE UNIQUE INDEX users_account_name_unique_index
ON users (account_name)
WHERE account_name IS NOT NULL;
