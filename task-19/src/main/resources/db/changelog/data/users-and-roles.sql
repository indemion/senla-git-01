INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_USER');

INSERT INTO users (username, password, created_at, updated_at) VALUES
('admin', '$2a$12$qu17/U7b4qdmCARHsmM.L.EJYOe9Gdq1FJGRVIXKvFtEScHwm5Bpe', now(), now()),
('user', '$2a$12$qu17/U7b4qdmCARHsmM.L.EJYOe9Gdq1FJGRVIXKvFtEScHwm5Bpe', now(), now());

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 2);