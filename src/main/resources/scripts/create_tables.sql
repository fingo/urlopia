CREATE TABLE users (
  id              SERIAL        PRIMARY KEY,
  principal_name  VARCHAR(63)   NOT NULL UNIQUE ,
  ad_name         VARCHAR(100)  NOT NULL UNIQUE ,
  mail            VARCHAR(63)   NOT NULL,
  first_name      VARCHAR(30),
  last_name       VARCHAR(30),
  admin           BOOLEAN       NOT NULL DEFAULT FALSE,
  leader          BOOLEAN       NOT NULL DEFAULT FALSE,
  b2b             BOOLEAN       NOT NULL DEFAULT FALSE,
  ec              BOOLEAN       NOT NULL DEFAULT TRUE,
  active          BOOLEAN       NOT NULL DEFAULT TRUE,
  lang            VARCHAR(2)    NOT NULL DEFAULT 'pl',
  work_time       REAL          DEFAULT 8.0
);

CREATE UNIQUE INDEX users_principal_name_index ON users(principal_name);
CREATE UNIQUE INDEX users_ad_name_index ON users(ad_name);
CREATE UNIQUE INDEX users_mail_index ON users(mail);

CREATE TABLE teams (
  name        VARCHAR(50)   PRIMARY KEY,
  ad_name     VARCHAR(100)  NOT NULL UNIQUE,
  leader_id   INT           REFERENCES users(id)
);

CREATE UNIQUE INDEX teams_ad_name_index ON teams(ad_name);

CREATE TABLE users_teams (
  id       SERIAL       PRIMARY KEY,
  user_id  INT          NOT NULL REFERENCES users(id),
  team_id  VARCHAR(50)  NOT NULL REFERENCES teams(name)
);

CREATE INDEX users_teams_user_index ON users_teams(user_id);
CREATE INDEX users_teams_team_index ON users_teams(team_id);

CREATE TABLE requests (
  id            SERIAL        PRIMARY KEY,
  created       TIMESTAMP     NOT NULL,
  modified      TIMESTAMP     NOT NULL,
  requester_id  INT           NOT NULL REFERENCES Users (id),
  start_date    DATE          NOT NULL,
  end_date      DATE          NOT NULL,
  working_days  INT           NOT NULL,
  type          VARCHAR(25)   NOT NULL DEFAULT 'NORMAL',
  type_info     VARCHAR(25),
  status        VARCHAR(25)   NOT NULL DEFAULT 'PENDING'
);

CREATE INDEX requests_modified_index ON requests(modified);
CREATE INDEX requests_requester_id_index ON requests(requester_id);

CREATE TABLE acceptances (
  id         SERIAL                         PRIMARY KEY,
  request_id INT REFERENCES requests (id)   NOT NULL,
  leader_id  INT REFERENCES users (id)      NOT NULL,
  status     VARCHAR(25)                    NOT NULL
);

CREATE INDEX acceptances_request_id_index ON acceptances(request_id);
CREATE INDEX acceptances_leader_id_index ON acceptances(leader_id);

CREATE TABLE history_logs (
  id                    SERIAL          PRIMARY KEY,
  created               TIMESTAMP       NOT NULL,
  user_id               INT             REFERENCES users (id) NOT NULL,
  decider_id            INT             REFERENCES users (id),
  request_id            INT             REFERENCES requests (id),
  hours                 DECIMAL(6, 2)   NOT NULL,
  hours_remaining       DECIMAL(6, 2)   NOT NULL DEFAULT 0,
  user_work_time        DECIMAL(4, 2)   NOT NULL,
  comment               VARCHAR(255)    NOT NULL DEFAULT '',
  prev_history_log_id   INT             REFERENCES history_logs (id) UNIQUE
);

CREATE INDEX history_logs_created_index ON history_logs(created);
CREATE INDEX history_logs_request_id_index ON history_logs(request_id);

CREATE TABLE holidays (
  id    SERIAL        PRIMARY KEY,
  name  VARCHAR(100)  NOT NULL,
  date  DATE          NOT NULL
);

ALTER SEQUENCE users_id_seq RESTART;

ALTER SEQUENCE requests_id_seq RESTART;

ALTER SEQUENCE acceptances_id_seq RESTART;

ALTER SEQUENCE history_logs_id_seq RESTART;

ALTER SEQUENCE holidays_id_seq RESTART;