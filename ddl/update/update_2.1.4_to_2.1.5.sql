ALTER TABLE index ADD COLUMN coefficient real;
ALTER TABLE index ADD COLUMN unit_of_measure character varying(255);
ALTER TABLE index ADD COLUMN decimals integer;

ALTER TABLE index_component DROP COLUMN start_time;
ALTER TABLE index_component DROP COLUMN end_time;
ALTER TABLE index_component RENAME COLUMN relative_time TO n_skip;
ALTER TABLE index_component RENAME COLUMN relative_period TO skip_period;

UPDATE index_component SET n_skip = NULL, skip_period = NULL;