INSERT INTO user (login, password, created_at, games_number, icon_id, money, email)
  SELECT * FROM (SELECT 'admin' as login, '$2a$10$YhK9ckpUMdx/WEwQfwMfpuwzJR81LvoQYxtqlec001jVjHhlMcNcq' as password, now() as created_at, 0 as games_number, 0 as icon_id, 0 as money, 'hedbanz.info@gmail.com' as email) AS tmp
  WHERE NOT EXISTS (
      SELECT login FROM user
  ) LIMIT 1;

INSERT INTO roles (name)
  SELECT * FROM (SELECT 'ROLE_USER') AS tmp
  WHERE NOT EXISTS (
      SELECT name FROM roles WHERE name = 'ROLE_USER'
  ) LIMIT 1;

INSERT INTO roles (name)
  SELECT * FROM (SELECT 'ROLE_ADMIN') AS tmp
  WHERE NOT EXISTS (
      SELECT name FROM roles WHERE name = 'ROLE_ADMIN'
  ) LIMIT 1;

INSERT INTO application (app_version)
  SELECT * FROM (SELECT 0) AS tmp
  WHERE NOT EXISTS (
      SELECT app_version FROM application
  ) LIMIT 1;

INSERT INTO advertise (delay, type)
  SELECT * FROM (SELECT 60, 1) as tmp
  WHERE NOT EXISTS(
      SELECT delay FROM advertise
  ) LIMIT 1;