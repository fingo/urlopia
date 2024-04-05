DELETE FROM presence_confirmations
WHERE id NOT IN (
    SELECT MIN(id)
    FROM presence_confirmations
    GROUP BY date, user_id
);

ALTER TABLE presence_confirmations
ADD CONSTRAINT pk_presence_confirmations PRIMARY KEY (date, user_id);