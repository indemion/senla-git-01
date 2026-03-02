INSERT INTO masters (firstname, lastname, status) VALUES
('Пётр', 'Петрович', 'free'),
('Афанасий', 'Афанасич', 'free'),
('Михал', 'Михалыч', 'free');

INSERT INTO garage_spots (number, status) VALUES
(1, 'free'),
(2, 'free'),
(3, 'free'),
(4, 'free'),
(5, 'free'),
(6, 'free'),
(7, 'free');

CALL fill_orders_random_data();