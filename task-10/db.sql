-- Active: 1766796267955@@127.0.0.1@5432@senlatask10
create table product(
    model varchar(50) primary key,
    maker varchar(10) not null,
    type varchar(50) not null
);

create table pc(
    code int primary key,
    model varchar(50) not null,
    speed smallint not null,
    ram smallint not null,
    hd real not null,
    cd varchar(10),
    price money,
    constraint fk_product
        foreign key(model)
            references product(model)
);

create table laptop(
    code int primary key,
    model varchar(50) not null,
    speed smallint not null,
    ram smallint not null,
    hd real not null,
    price money,
    screen smallint not null,
    constraint fk_product
        foreign key(model)
            references product(model)
);

create table printer(
    code int primary key,
    model varchar(50) not null,
    color char(1) not null,
    type varchar(10) not null,
    price money,
    constraint fk_product
        foreign key(model)
            references product(model)
);

INSERT INTO product (maker, model, type) VALUES
('Acer', 'Aspire 3', 'Laptop'),
('Dell', 'Vostro 3500', 'Laptop'),
('HP', 'Pavilion 15', 'Laptop'),
('Lenovo', 'ThinkPad X1', 'Laptop'),
('ASUS', 'TUF Gaming', 'Laptop'),
('Intel', 'Core i5-12400', 'PC'),
('AMD', 'Ryzen 5 5600X', 'PC'),
('Apple', 'Mac Mini M1', 'PC'),
('Epson', 'L3100', 'Printer'),
('Canon', 'E414', 'Printer'),
('HP', 'LaserJet Pro M15w', 'Printer'),
('B', '11111', 'PC'),
('A', '22222', 'PC'),
('Intel', '33333', 'PC'),
('Intel', '44444', 'PC'),
('Intel', '55555', 'Laptop'),
('HP', '66666', 'PC');

INSERT INTO pc (code, model, speed, ram, hd, cd, price) VALUES
(1, 'Core i5-12400', 2400, 16, 512, '8x', 1500),
(2, 'Ryzen 5 5600X', 3500, 32, 1024, '8x', 1700),
(3, 'Mac Mini M1', 3900, 8, 256, null, 2500),
(4, '11111', 1400, 8, 256, '12x', 490),
(5, '22222', 1400, 8, 256, '4x', 350),
(6, '33333', 2000, 8, 512, '8x', 550),
(7, '44444', 2400, 8, 256, '8x', 485),
(8, '66666', 5000, 4, 256, '8x', 670);

INSERT INTO laptop (code, model, speed, ram, hd, price, screen) VALUES
(1, 'Aspire 3', 1600, 8, 512, 1000, 15),
(2, 'Vostro 3500', 2200, 16, 1000, 1500, 15),
(3, 'Pavilion 15', 2400, 8, 512, 1200, 15),
(4, 'ThinkPad X1', 2600, 16, 512, 2500, 14),
(5, 'TUF Gaming', 3200, 16, 1024, 2500, 17),
(6, '55555', 2300, 8, 512, 2300, 15);

INSERT INTO printer (code, model, color, type, price) VALUES
(1, 'L3100', 'y', 'Jet', 150),
(2, 'E414', 'n', 'Laser', 120),
(3, 'LaserJet Pro M15w', 'n', 'Laser', 180);

-- Найти номер модели, скорость и размер жесткого диска для всех ПК стоимостью менее 500 долларов.
SELECT model, speed, hd FROM pc WHERE price < 500::money;

-- Найти производителей принтеров. Вывести поля: maker.
SELECT maker FROM product WHERE type = 'Printer';

-- Найти номер модели, объем памяти и размеры экранов ноутбуков, цена которых превышает 1000 долларов.
SELECT model, ram, screen FROM laptop WHERE price > 1000::money;

-- Найти все записи таблицы Printer для цветных принтеров.
SELECT * FROM printer WHERE color = 'y';

-- Найти номер модели, скорость и размер жесткого диска для ПК, имеющих скорость cd 12x или 24x и цену менее 600 долларов.
SELECT model, speed, hd FROM pc WHERE cd IN ('12x', '24x') AND price < 600::money;

-- Указать производителя и скорость для тех ноутбуков, которые имеют жесткий диск объемом не менее 100 Гбайт.
SELECT maker, speed
FROM laptop
JOIN product ON product.model = laptop.model
WHERE hd >= 100;

-- Найти номера моделей и цены всех продуктов (любого типа), выпущенных производителем B (латинская буква).
SELECT p.model, price
FROM product
JOIN (
    SELECT model, price FROM pc
    UNION
    SELECT model, price FROM laptop
    UNION
    SELECT model, price FROM printer
) AS p ON p.model = product.model
WHERE maker = 'B';

-- Найти производителя, выпускающего ПК, но не ноутбуки.
SELECT DISTINCT maker
FROM product p1
WHERE type = 'PC' AND NOT EXISTS(SELECT 1 FROM product WHERE maker = p1.maker AND type = 'Laptop');

-- Найти производителей ПК с процессором не менее 450 Мгц. Вывести поля: maker.
SELECt DISTINCT maker
FROM product
JOIN pc ON pc.model = product.model
WHERE speed >= 450;

-- Найти принтеры, имеющие самую высокую цену. Вывести поля: model, price.
SELECT model, price 
FROM printer 
WHERE price = (SELECT MAX(price) FROM printer);

-- Найти среднюю скорость ПК.
SELECT ROUND(AVG(speed)) AS avg_speed FROM pc;

-- Найти среднюю скорость ноутбуков, цена которых превышает 1000 долларов.
SELECT ROUND(AVG(speed)) AS avg_speed FROM laptop WHERE price > 1000::money;

-- Найти среднюю скорость ПК, выпущенных производителем A.
SELECT ROUND(AVG(speed)) AS avg_speed
FROM pc
JOIN product ON product.model = pc.model
WHERE product.maker = 'A';

-- Для каждого значения скорости процессора найти среднюю стоимость ПК с такой же скоростью. Вывести поля: скорость, средняя цена.
SELECT speed, AVG(price::numeric)::money AS avg_price FROM pc GROUP BY speed;

-- Найти размеры жестких дисков, совпадающих у двух и более PC. Вывести поля: hd.
SELECT hd FROM pc GROUP BY hd HAVING count(hd) >= 2;

/* Найти пары моделей PC, имеющих одинаковые скорость процессора и RAM. В результате каждая пара указывается только один раз, 
т.е. (i,j), но не (j,i), Порядок вывода полей: модель с большим номером, модель с меньшим номером, скорость, RAM. */
SELECT
    CASE WHEN p1.model > p2.model THEN p1.model ELSE p2.model END,
    CASE WHEN p1.model > p2.model THEN p2.model ELSE p1.model END,
    p1.speed,
    p1.ram
FROM pc p1
JOIN pc p2 ON (p2.speed = p1.speed AND p2.ram = p1.ram)
WHERE p1.model <> p2.model AND p1.model > p2.model;

-- Найти модели ноутбуков, скорость которых меньше скорости любого из ПК. Вывести поля: type, model, speed.
SELECT type, l.model, l.speed
FROM laptop l
JOIN product p ON p.model = l.model
WHERE speed < (SELECT MIN(speed) FROM pc);

-- Найти производителей самых дешевых цветных принтеров. Вывести поля: maker, price.
SELECT DISTINCT maker, price
FROM printer p1
JOIN product p2 ON p2.model = p1.model
WHERE color = 'y' AND price = (SELECT MIN(price) FROM printer WHERE color = 'y');

-- Для каждого производителя найти средний размер экрана выпускаемых им ноутбуков. Вывести поля: maker, средний размер экрана.
SELECT maker, ROUND(AVG(screen), 1) AS avg_screen
FROM laptop l
JOIN product p ON p.model = l.model
GROUP BY maker;

-- Найти производителей, выпускающих по меньшей мере три различных модели ПК. Вывести поля: maker, число моделей.
SELECT maker, COUNT(model) AS model_count
FROM product
WHERE type = 'PC'
GROUP BY maker
HAVING COUNT(model) >= 3;

-- Найти максимальную цену ПК, выпускаемых каждым производителем. Вывести поля: maker, максимальная цена.
SELECT maker, MAX(price) AS max_price
FROM pc
JOIN product p ON p.model = pc.model
GROUP BY maker;

-- Для каждого значения скорости процессора ПК, превышающего 600 МГц, найти среднюю цену ПК с такой же скоростью. Вывести поля: speed, средняя цена.
SELECT speed, AVG(price::numeric)::money AS avg_price
FROM pc
WHERE speed > 600
GROUP BY speed;

-- Найти производителей, которые производили бы как ПК, так и ноутбуки со скоростью не менее 750 МГц. Вывести поля: maker
SELECT maker
FROM product p1
JOIN pc p2 ON p2.model = p1.model
WHERE speed >= 750
INTERSECT
SELECT maker
FROM product p1
JOIN laptop p2 ON p2.model = p1.model
WHERE speed >= 750;

-- Перечислить номера моделей любых типов, имеющих самую высокую цену по всей имеющейся в базе данных продукции.
WITH all_products AS (
    SELECT model, price FROM pc
    UNION
    SELECT model, price FROM laptop
    UNION
    SELECT model, price FROM printer
)
SELECT model 
FROM all_products
WHERE price = (SELECT MAX(price) FROM all_products);

/* Найти производителей принтеров, которые производят ПК с наименьшим объемом RAM и с самым быстрым процессором среди всех ПК, 
имеющих наименьший объем RAM. Вывести поля: maker */
WITH min_ram_pc AS (
    SELECT *
    FROM pc
    WHERE ram = (SELECT MIN(ram) FROM pc)
)
SELECT DISTINCT maker
FROM product
WHERE 
    type = 'PC' 
    AND maker IN (SELECT maker FROM product WHERE type = 'Printer')
    AND model IN (
        SELECT model
        FROM min_ram_pc
        WHERE speed = (SELECT MAX(speed) FROM min_ram_pc)
    );
