DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'presence_confirmations'
        AND column_name = 'id'
    ) THEN
        -- Remove duplicate presence confirmations
        DELETE FROM presence_confirmations
        WHERE id NOT IN (
            SELECT MIN(id)
            FROM presence_confirmations
            GROUP BY date, user_id
        );

        -- Add composite pkey
        ALTER TABLE presence_confirmations
        ADD CONSTRAINT pk_presence_confirmations PRIMARY KEY (date, user_id);

        -- Drop id column
        ALTER TABLE presence_confirmations
        DROP COLUMN id;
    END IF;
END $$;