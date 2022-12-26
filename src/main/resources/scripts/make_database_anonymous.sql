----Automatic_vacation_days

update automatic_vacation_days set created = now(), modified = now(), next_year_days_base = 26, next_year_hours_proposition = 0;

----

---- History Logs

update history_logs  set created = now(), "comment" = 'Lorem ipsum';

----


---- Single_day_hour_preference

update single_day_hour_preference  set non_working  = false, start_time = '08:00:00', end_time = '16:00:00';

----

---user_working_hours_preference

update user_working_hours_preference set changed = '01-01-2021';


--- teams

do $$

DECLARE
_team_number_proposition integer array;
_teams_size integer;
_extended_teams_size integer;
_random_index integer;
_value_from_proposition integer;
_team record;
_new_team_name text;
_team_leader_id integer;


BEGIN

select count(*)
into _teams_size
from teams t;

_extended_teams_size := _teams_size  * 100;

for i in 1.._extended_teams_size loop
select array_append(_team_number_proposition, i)
into _team_number_proposition;
end loop;

for _team in select *  from teams t loop
   		_random_index := floor(random() * _extended_teams_size  + 1);
_value_from_proposition = _team_number_proposition[_random_index];
		_new_team_name := 'Random Team' || _value_from_proposition ;
		_team_leader_id  := _team.leader_id;

select array_remove(_team_number_proposition, _value_from_proposition) into _team_number_proposition;
_extended_teams_size := _extended_teams_size -1;

insert into teams values (_new_team_name, _new_team_name, _team_leader_id);
insert into users_teams (user_id, team_id) select ut.user_id , _new_team_name from users_teams ut where team_id = _team.name ;
delete from users_teams ut where ut.team_id  = _team.name;
delete from teams t where t."name"  = _team.name;

end loop;

end $$;


---- user



do $$

DECLARE
_users_number_proposition integer array;
_users_size integer;
_extended_users_size integer;
_random_index integer;
_value_from_proposition integer;
_u_user record;
_new_user_name text;
_new_user_surname text;
_new_user_ad_name text;


BEGIN

select count(*)
into _users_size
from users u ;

_extended_users_size := _users_size  * 100;

for i in 1.._extended_users_size loop
select array_append(_users_number_proposition, i)
into _users_number_proposition;
end loop;

for _u_user in select *  from users u loop
   		_random_index := floor(random() * _extended_users_size  + 1);
_value_from_proposition = _users_number_proposition[_random_index];
		_new_user_name := 'name' || _value_from_proposition ;
		_new_user_surname := 'surname' || _value_from_proposition ;
		_new_user_ad_name := _new_user_name  || '.' || _new_user_surname || '@example.com';

select array_remove(_users_number_proposition, _value_from_proposition) into _users_number_proposition;
_extended_users_size := _extended_users_size -1;

update users u set first_name = _new_user_name , last_name  = _new_user_surname, mail = _new_user_ad_name,
                   ad_name = _new_user_ad_name , principal_name  = _new_user_ad_name, admin=false, work_time =8
where u.id = _u_user.id ;


end loop;

end $$;


--- presence confirm + request


do $$

DECLARE
_u_user record;
_should_subtract boolean;
_random_days_number integer;

BEGIN

for _u_user in select *  from users u loop

   		_should_subtract := random() > 0.5;
_random_days_number := floor(random() * 13  + 1);

   		if _should_subtract then
   			_random_days_number := _random_days_number * -1;
end if;

   		-- presence_confirm
update presence_confirmations pc set start_time = '08:00', end_time = '16:00', date = date + _random_days_number where pc.user_id  = _u_user.id;

-- requests
update requests set created = now(), modified = now(), type_info = 'OTHER', start_date = start_date  +_random_days_number, end_date = end_date + _random_days_number ;

end loop;

end $$;
