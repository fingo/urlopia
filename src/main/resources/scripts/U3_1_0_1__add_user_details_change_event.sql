alter table history_logs drop column user_details_change_event ;

DELETE FROM history_logs
WHERE user_details_change_event = 'USER_CHANGE_WORK_TIME'
  AND "comment" LIKE 'Zmieniono etat z%';