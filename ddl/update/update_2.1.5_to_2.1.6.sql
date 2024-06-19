-- ADDING COLUMN diff_drain_id --
ALTER TABLE drain ADD COLUMN diff_drain_id integer;
ALTER TABLE drain ADD CONSTRAINT drain_diff_drain_fkey FOREIGN KEY (diff_drain_id) REFERENCES drain (id) ON UPDATE CASCADE ON DELETE CASCADE;

-- ADDING COLUMN end_time IN DASHBOARD WIDGET --
ALTER TABLE dashboard_widget ADD COLUMN end_time timestamp with time zone;