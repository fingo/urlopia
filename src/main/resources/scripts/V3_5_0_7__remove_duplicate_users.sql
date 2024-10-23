ALTER TABLE acceptances DROP CONSTRAINT acceptances_leader_id_fkey;
ALTER TABLE acceptances ADD CONSTRAINT acceptances_leader_id_fkey FOREIGN KEY (leader_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE automatic_vacation_days DROP CONSTRAINT automatic_vacation_days_user_id_fkey;
ALTER TABLE automatic_vacation_days ADD CONSTRAINT automatic_vacation_days_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE history_logs DROP CONSTRAINT history_logs_user_id_fkey;
ALTER TABLE history_logs ADD CONSTRAINT history_logs_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE history_logs DROP CONSTRAINT history_logs_decider_id_fkey;
ALTER TABLE history_logs ADD CONSTRAINT history_logs_user_id_fkey FOREIGN KEY (decider_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE presence_confirmations DROP CONSTRAINT presence_confirmations_user_id_fkey;
ALTER TABLE presence_confirmations ADD CONSTRAINT presence_confirmations_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE requests DROP CONSTRAINT requests_requester_id_fkey;
ALTER TABLE requests ADD CONSTRAINT requests_requester_id_fkey FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE teams DROP CONSTRAINT teams_leader_id_fkey;
ALTER TABLE teams ADD CONSTRAINT teams_leader_id_fkey FOREIGN KEY (leader_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE user_working_hours_preference DROP CONSTRAINT user_working_hours_preference_user_id_fkey;
ALTER TABLE user_working_hours_preference ADD CONSTRAINT user_working_hours_preference_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE users_teams DROP CONSTRAINT users_teams_user_id_fkey;
ALTER TABLE users_teams ADD CONSTRAINT users_teams_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

WITH CTE AS (SELECT id, ROW_NUMBER() OVER (PARTITION BY account_name ORDER BY id) AS rn FROM users)
DELETE FROM users
WHERE id IN (SELECT id FROM CTE WHERE rn > 1);