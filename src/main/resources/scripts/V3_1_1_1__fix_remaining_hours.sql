do $$

declare
_users_to_fix integer array;
	_logs_to_fix integer array;
	_log_id integer;
	_user_id integer;
	_new_hours_remaining numeric(6,2) ;
	_hours_from_log numeric(6,2) ;

BEGIN

-- set valid hours_remaining to user_change_work_time logs

update history_logs
set hours  = 0,
    hours_remaining = q3.hours_remaining

    from
	-- this subquery find what was last valid hours_remaining from last log before change_work_time
	(select hl3.hours_remaining,
			hl3.user_id,
			q2.max_log_before
	 from
	 	history_logs hl3
	 	join
	 	-- this subquery find last log for user before change_work_time event
		(select hl.user_id,
				q1.min_work_time,
				(select max(id) as max_log_before from history_logs hl2  where id < min_work_time and hl2.user_id = hl.user_id)
		from
			history_logs hl
			join
			-- this subquery find first user_change_work_time for every user that has one
			(select user_id, min(id) as min_work_time from history_logs hl
			where user_details_change_event = 'USER_CHANGE_WORK_TIME'
			group by user_id) q1
			on q1.user_id = hl.user_id
		group by hl.user_id, q1.min_work_time) q2
		on hl3.id = max_log_before) as q3

where user_details_change_event = 'USER_CHANGE_WORK_TIME' and history_logs.user_id  = q3.user_id;


--- find users that has any logs after work_time_change event

_users_to_fix := array(select distinct hl3.user_id
		from
			history_logs hl3
			join
			(select hl.user_id,
					q1.max_work_time_event,
					(select min(id) as min_log_after from history_logs hl2  where id > max_work_time_event and hl2.user_id = hl.user_id)
			from
				history_logs hl
				join
				(select user_id, max(id) as max_work_time_event from history_logs hl
					where user_details_change_event = 'USER_CHANGE_WORK_TIME'
					group by user_id) q1
				on q1.user_id = hl.user_id
			group by hl.user_id, q1.max_work_time_event) q2
			on hl3.user_id = q2.user_id
		where hl3.id >= q2.min_log_after
		order by hl3.user_id);

-- iterate over users

FOREACH _user_id IN ARRAY _users_to_fix
   LOOP

   		-- find logs to fix by given users (logs after last work_time_change)

		_logs_to_fix  := array(
		select distinct hl3.id
		from
			history_logs hl3
			join
			(select hl.user_id,
					q1.max_work_time_event,
					(select min(id) as min_log_after from history_logs hl2  where id > max_work_time_event and hl2.user_id = hl.user_id)
			from
				history_logs hl
				join
				(select user_id, max(id) as max_work_time_event from history_logs hl
					where user_details_change_event = 'USER_CHANGE_WORK_TIME'
					group by user_id) q1
				on q1.user_id = hl.user_id
			group by hl.user_id, q1.max_work_time_event) q2
			on hl3.user_id = q2.user_id
		where hl3.id >= q2.min_log_after and q2.user_id = _user_id
		order by hl3.id);

   		-- get last valid remaining_hours for user

select hl.hours_remaining
into _new_hours_remaining
from history_logs hl
where user_id = _user_id  and user_details_change_event = 'USER_CHANGE_WORK_TIME';

FOREACH _log_id IN ARRAY _logs_to_fix
  		loop

  		   -- get hour change from log

select hl.hours
into _hours_from_log
from history_logs hl
where id = _log_id;

-- add it to valid remaining hours from log before

update history_logs
set hours_remaining = _new_hours_remaining + hours
where id = _log_id;

-- update remainig hours base

_new_hours_remaining :=  _new_hours_remaining + _hours_from_log  ;

END LOOP;
END LOOP;

END
$$