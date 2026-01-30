-- Active: 1766796267955@@127.0.0.1@5432@senlatask11
DROP TABLE IF EXISTS garage_spots CASCADE;
CREATE TABLE garage_spots (
  id serial PRIMARY KEY,
  number integer NOT NULL,
  status varchar NOT NULL
    CHECK (status IN ('free', 'occupied')),
  order_at_work_id integer
);

DROP TABLE IF EXISTS masters CASCADE;
CREATE TABLE masters (
  id serial PRIMARY KEY,
  firstname varchar NOT NULL,
  lastname varchar NOT NULL,
  status varchar NOT NULL
    CHECK (status IN ('free', 'busy')),
  order_at_work_id integer
);

DROP TABLE IF EXISTS orders CASCADE;
CREATE TABLE orders (
  id serial PRIMARY KEY,
  price integer NOT NULL,
  master_id integer NOT NULL,
  garage_spot_id integer NOT NULL,
  status varchar NOT NULL
    CHECK (status IN ('created', 'work_in_progress', 'closed', 'canceled', 'deleted')),
  estimated_work_period_start timestamp(0),
  estimated_work_period_end timestamp(0),
  actual_work_period_start timestamp(0),
  actual_work_period_end timestamp(0),
  created_at timestamp(0),
  closed_at timestamp(0),
  canceled_at timestamp(0),
  deleted_at timestamp(0)
);

ALTER TABLE orders
ADD CONSTRAINT fk_orders_master_id
FOREIGN KEY (master_id) 
REFERENCES masters (id);

ALTER TABLE orders
ADD CONSTRAINT fk_orders_garage_spot_id
FOREIGN KEY (garage_spot_id) 
REFERENCES garage_spots (id);

ALTER TABLE masters
ADD CONSTRAINT fk_masters_order_at_work_id
FOREIGN KEY (order_at_work_id) 
REFERENCES orders (id) ON DELETE SET NULL;

ALTER TABLE garage_spots
ADD CONSTRAINT fk_garage_spots_order_at_work_id
FOREIGN KEY (order_at_work_id) 
REFERENCES orders (id) ON DELETE SET NULL;

-- Процедуры и функции
CREATE OR REPLACE PROCEDURE fill_orders_random_data()
LANGUAGE plpgsql
AS $$
DECLARE
  i INTEGER;
  v_estimated_work_period_start timestamp;
  v_estimated_work_period_end timestamp;
  v_master_id integer;
  v_garage_spot_id integer;
  v_status varchar;
  new_order_id integer;
BEGIN
  FOR i IN 1..10 LOOP
    v_estimated_work_period_start := (NOW()::timestamp) + (i * INTERVAL '1 hour');
    v_estimated_work_period_end := v_estimated_work_period_start + INTERVAL '1 hour';
    SELECT id INTO v_master_id 
    FROM get_free_master_id_in_period(v_estimated_work_period_start, v_estimated_work_period_end);
    SELECT id INTO v_garage_spot_id 
    FROM get_free_garage_spot_id_in_period(v_estimated_work_period_start, v_estimated_work_period_end);
    v_status := 'created';

    INSERT INTO orders (
      price, 
      master_id, 
      garage_spot_id, 
      status, 
      estimated_work_period_start, 
      estimated_work_period_end, 
      actual_work_period_start, 
      actual_work_period_end, 
      created_at
    ) VALUES (
      FLOOR(random() * 1400 + 100),
      v_master_id,
      v_garage_spot_id,
      v_status,
      v_estimated_work_period_start,
      v_estimated_work_period_end,
      null,
      null,
      NOW()
    )
    RETURNING id INTO new_order_id;
  END LOOP;
END;
$$;

CREATE OR REPLACE FUNCTION get_free_master_id_in_period(filter_estimated_work_period_start timestamp, filter_estimated_work_period_end timestamp)
RETURNS TABLE (id integer)
LANGUAGE plpgsql
AS $$
BEGIN
  RETURN QUERY
  SELECT masters.id 
  FROM masters
  WHERE NOT EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.master_id = masters.id
      AND o.estimated_work_period_start < filter_estimated_work_period_end
      AND o.estimated_work_period_end > filter_estimated_work_period_start
  )
  ORDER BY RANDOM()
  LIMIT 1;
END;
$$;

CREATE OR REPLACE FUNCTION get_free_garage_spot_id_in_period(filter_estimated_work_period_start timestamp, filter_estimated_work_period_end timestamp)
RETURNS TABLE (id integer)
LANGUAGE plpgsql
AS $$
BEGIN
  RETURN QUERY
  SELECT gs.id 
  FROM garage_spots gs
  WHERE NOT EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.garage_spot_id = gs.id
      AND o.estimated_work_period_start < filter_estimated_work_period_end
      AND o.estimated_work_period_end > filter_estimated_work_period_start
  )
  ORDER BY RANDOM()
  LIMIT 1;
END;
$$;

CREATE OR REPLACE FUNCTION set_garage_spot_status_to_free()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  IF OLD.order_at_work_id IS NOT NULL AND NEW.order_at_work_id IS NULL THEN
      NEW.status := 'free';
  END IF;
  RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION set_master_status_to_free()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  IF OLD.order_at_work_id IS NOT NULL AND NEW.order_at_work_id IS NULL THEN
      NEW.status := 'free';
  END IF;
  RETURN NEW;
END;
$$;
  
CREATE OR REPLACE FUNCTION check_master_single_order_at_work()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.status = 'work_in_progress' AND OLD.status != 'work_in_progress' THEN
        IF EXISTS (
            SELECT 1 FROM orders
            WHERE master_id = NEW.master_id
              AND status = 'work_in_progress'
              AND id != NEW.id
        ) THEN
            RAISE EXCEPTION 'Мастер уже выполняет заказ и не может взять новый';
        END IF;
    END IF;
    RETURN NEW;
END;
$$;

-- Триггеры
CREATE OR REPLACE TRIGGER trigger_set_garage_spot_free
  BEFORE UPDATE OF order_at_work_id ON garage_spots
  FOR EACH ROW
  EXECUTE FUNCTION set_garage_spot_status_to_free();


CREATE OR REPLACE TRIGGER trigger_set_master_free
  BEFORE UPDATE OF order_at_work_id ON masters
  FOR EACH ROW
  EXECUTE FUNCTION set_master_status_to_free();

CREATE OR REPLACE TRIGGER trigger_check_master_single_order_at_work
  BEFORE UPDATE ON orders
  FOR EACH ROW
  EXECUTE FUNCTION check_master_single_order_at_work();

-- Данные
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