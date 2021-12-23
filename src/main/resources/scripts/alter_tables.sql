ALTER TABLE teams DROP COLUMN IF EXISTS business_part_leader_id;

ALTER TABLE requests ALTER COLUMN type_info TYPE varchar(60);
