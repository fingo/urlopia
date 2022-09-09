CREATE TABLE single_day_hour_preference
(
    id          SERIAL PRIMARY KEY,
    non_working BOOLEAN NOT NULL DEFAULT FALSE,
    start_time  TIME    NOT NULL,
    end_time    TIME    NOT NULL
);

CREATE TABLE user_working_hours_preference
(
    user_id                 INT PRIMARY KEY REFERENCES users (id),
    changed                 TIMESTAMP NOT NULL,
    monday_preference_id    INT REFERENCES single_day_hour_preference (id),
    tuesday_preference_id   INT REFERENCES single_day_hour_preference (id),
    wednesday_preference_id INT REFERENCES single_day_hour_preference (id),
    thursday_preference_id  INT REFERENCES single_day_hour_preference (id),
    friday_preference_id    INT REFERENCES single_day_hour_preference (id)
);

ALTER SEQUENCE single_day_hour_preference_id_seq RESTART;
