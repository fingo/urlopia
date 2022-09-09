CREATE TABLE presence_confirmations
(
    date       DATE,
    user_id    INT REFERENCES users (id),
    start_time TIME NOT NULL,
    end_time   TIME NOT NULL,
    PRIMARY KEY (date, user_id)
);