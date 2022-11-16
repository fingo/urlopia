CREATE TABLE automatic_vacation_days
(
    id                          INT PRIMARY KEY,
    created                     TIMESTAMP NOT NULL,
    next_year_days_base         INT NOT NULL DEFAULT 26,
    next_year_hours_proposition DECIMAL(6, 2) NOT NULL DEFAULT 0,
    user_id                     INT REFERENCES users (id) NOT NULL
);
CREATE SEQUENCE seq_automatic_vacation_days;

INSERT INTO automatic_vacation_days (id, created, user_id)
SELECT nextval('seq_automatic_vacation_days'), CURRENT_TIMESTAMP, id  FROM users;