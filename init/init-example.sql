-- создаём базу
CREATE DATABASE bid_flux_db;

-- создаём пользователя
CREATE USER test_user WITH PASSWORD 'example_password';

-- права на базу (подключение и т.д.)
GRANT CONNECT ON DATABASE bid_flux_db TO test_user;

\c bid_flux_db  -- переключаемся в новую БД

-- права на таблицу auction: полный доступ
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE, REFERENCES, TRIGGER
      ON TABLE auction TO test_user;

REVOKE ALL PRIVILEGES ON TABLE bid FROM test_user;

-- права на таблицу bid: только SELECT и INSERT
GRANT SELECT, INSERT ON TABLE bid TO test_user;
