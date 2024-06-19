UPDATE dashboard_widget_detail SET operator = 'SEMICOLON' WHERE formula_id IS NOT NULL;

ALTER TABLE application_user ADD COLUMN default_start timestamp with time zone;
ALTER TABLE application_user ADD COLUMN default_end timestamp with time zone;
ALTER TABLE application_user ADD COLUMN drain_tree_depth character varying(20);

UPDATE application_user SET drain_tree_depth = 'org';

ALTER TABLE application_user ALTER COLUMN drain_tree_depth SET NOT NULL;

ALTER TABLE drain ADD COLUMN positive_negative_value boolean NOT NULL DEFAULT FALSE;

ALTER TABLE formula_component ADD COLUMN positive_negative_value varchar(10);

ALTER TABLE dashboard_widget_detail ADD COLUMN positive_negative_value varchar(10);

ALTER TABLE drain ADD COLUMN max_value double precision;

ALTER TABLE drain ADD COLUMN min_value double precision;

ALTER TABLE dashboard_widget_detail ADD COLUMN exclude_outliers boolean;

ALTER TABLE formula_component ADD COLUMN exclude_outliers boolean;

UPDATE formula_component SET exclude_outliers = false;

UPDATE dashboard_widget_detail SET exclude_outliers = false;

ALTER TABLE application_user ADD COLUMN dark_theme boolean DEFAULT false;

UPDATE application_user SET dark_theme = false;
