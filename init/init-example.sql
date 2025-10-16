GRANT CONNECT ON DATABASE bid_flux_db TO bidflux_test_user;

CREATE TABLE auction (
                         id uuid PRIMARY KEY,
                         owner_id uuid,
                         status varchar(60) not null
);

CREATE TABLE bid (
                     id uuid PRIMARY KEY,
                     auction_id uuid not null,
                     user_id uuid not null,
                     amount numeric(19,4) not null ,
                     created_at timestamp not null default CURRENT_TIMESTAMP
);

CREATE TABLE users (
                       id uuid PRIMARY KEY
);

CREATE TABLE archived_auctions (
                                   owner_id uuid not null,
                                   winner_id uuid not null
);

GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE, REFERENCES, TRIGGER
    ON TABLE auction TO bidflux_test_user;

REVOKE ALL PRIVILEGES ON TABLE bid FROM bidflux_test_user;

GRANT SELECT, INSERT ON TABLE bid TO bidflux_test_user;
