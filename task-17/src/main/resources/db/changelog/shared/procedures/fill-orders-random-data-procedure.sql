CREATE OR REPLACE PROCEDURE fill_orders_random_data()
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
$$ LANGUAGE plpgsql;