CREATE OR REPLACE FUNCTION get_free_master_id_in_period(filter_estimated_work_period_start timestamp, filter_estimated_work_period_end timestamp)
RETURNS TABLE (id integer)
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
$$ LANGUAGE plpgsql;