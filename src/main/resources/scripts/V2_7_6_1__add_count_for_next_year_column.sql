ALTER TABLE history_logs ADD COLUMN count_for_next_year BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE history_logs SET count_for_next_year = true WHERE comment LIKE '%wymiar na%';