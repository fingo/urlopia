ALTER TABLE history_logs ADD COLUMN user_details_change_event varchar(60) DEFAULT NULL;

INSERT INTO history_logs (created, user_id, user_work_time, hours,  "comment",	user_details_change_event)
SELECT hl2.created - interval '2 seconds', hl2.user_id, hl2.user_work_time, hl2.hours, CONCAT('Zmieniono etat z: ', ROUND(CAST(hl.user_work_time AS numeric) / 8.0 , 2) , ' na ', ROUND(CAST(hl2.user_work_time AS numeric) / 8.0 , 2)), 'USER_CHANGE_WORK_TIME'
FROM history_logs hl
    JOIN history_logs hl2 ON (hl.id = hl2.prev_history_log_id AND hl.user_id = hl2.user_id )
WHERE hl.user_work_time != hl2.user_work_time;