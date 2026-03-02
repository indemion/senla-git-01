CREATE OR REPLACE FUNCTION get_free_garage_spot_id_in_period(filter_estimated_work_period_start timestamp, filter_estimated_work_period_end timestamp)
RETURNS TABLE (id integer)
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
$$ LANGUAGE plpgsql;