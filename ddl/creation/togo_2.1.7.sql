-- Table of roles --
CREATE TABLE role (
    id serial,
    name character varying(45) NOT NULL UNIQUE,
    description character varying(255)
);
ALTER TABLE role OWNER TO togo;

ALTER TABLE ONLY role ADD CONSTRAINT role_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY role ADD CONSTRAINT role_name_ukey UNIQUE (name);

-- Table of application users --
CREATE  TABLE application_user (
    id serial,
    username character varying(45) NOT NULL,
    name character varying(100),
    surname character varying(100),
    password character varying(160) NOT NULL,
    email character varying(100) NOT NULL,
    avatar bytea,
    style character varying(50),
    lang character varying(5),
    enabled integer NOT NULL DEFAULT 0,
    default_start timestamp with time zone,
    default_end timestamp with time zone,
    drain_tree_depth character varying(20) NOT NULL,
    dark_theme boolean DEFAULT false
);
ALTER TABLE application_user OWNER TO togo;

ALTER TABLE ONLY application_user ADD CONSTRAINT user_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY application_user ADD CONSTRAINT application_user_username_ukey UNIQUE (username);
ALTER TABLE ONLY application_user ADD CONSTRAINT application_user_email_ukey UNIQUE (email);

-- Table of roles for users --
CREATE TABLE users_roles (
    role_id integer NOT NULL,
    user_id integer NOT NULL
);
ALTER TABLE users_roles OWNER TO togo;

ALTER TABLE ONLY users_roles ADD CONSTRAINT users_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES role (id);
ALTER TABLE ONLY users_roles ADD CONSTRAINT users_roles_user_id_fkey FOREIGN KEY (user_id) REFERENCES application_user (id);

-- Roles and default admin user --
INSERT INTO application_user (username, password, enabled, email) VALUES ('admin', 'PBKDF2$sha512$100000$LVNAfWF2GAdHpGD34SbuWg==$kZJDqUF8NoMWDwRbBUduCNz95KT6BfmmScucNKSPYvgGsKauLz5u83J2mBqW/4r98heHczvyvQzS/B/XYY+Chw==', 1, 'admin@admin.it');
INSERT INTO role (name, description) VALUES ('ROLE_ADMIN', 'I can do everithing'), ('ROLE_USER', 'I have access to my orgs'), ('ROLE_USER_RO', 'I have read only access to my orgs');
INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);

-- Table of organizations --
CREATE TABLE org (
    id serial,
    name character varying(255) NOT NULL,
    parent_id integer
);
ALTER TABLE org OWNER TO togo;

ALTER TABLE ONLY org ADD CONSTRAINT org_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY org ADD CONSTRAINT org_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES org (id);

-- Job Table --
CREATE TABLE job (
    id serial,
    name character varying(255) NOT NULL,
    description character varying(255) NOT NULL,
    org_id integer NOT NULL
);
ALTER TABLE job OWNER TO togo;

ALTER TABLE ONLY job ADD CONSTRAINT job_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY job ADD CONSTRAINT job_org_id_fkey FOREIGN KEY (org_id) REFERENCES org (id);

-- Table of jobs for users --
CREATE TABLE users_jobs (
    user_id integer NOT NULL,
    job_id integer NOT NULL
);
ALTER TABLE users_jobs OWNER TO togo;

ALTER TABLE ONLY users_jobs ADD CONSTRAINT users_jobs_user_id_fkey FOREIGN KEY (user_id) REFERENCES application_user (id);
ALTER TABLE ONLY users_jobs ADD CONSTRAINT users_jobs_job_id_fkey FOREIGN KEY (job_id) REFERENCES job (id);

-- Table of client categories --
CREATE TABLE client_category (
    id serial,
    description text,
    image bytea
);
ALTER TABLE client_category OWNER TO togo;

ALTER TABLE ONLY client_category ADD CONSTRAINT client_category_id_pkey PRIMARY KEY (id);

-- Table of clients --
CREATE TABLE client (
    id serial,
    name character varying(255) NOT NULL,
    client_category_id integer,
    type character varying(255) NOT NULL DEFAULT 'GENERIC'::character varying,
    org_id integer NOT NULL,
    parent_id integer,
    computer_client boolean,
    energy_client boolean,
    device_id character varying(50),
    image bytea,
    active boolean DEFAULT true,
    plugin_id character varying(255),
    controller_id integer
);
ALTER TABLE client OWNER TO togo;

ALTER TABLE ONLY client ADD CONSTRAINT client_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY client ADD CONSTRAINT client_client_category_id_fkey FOREIGN KEY (client_category_id) REFERENCES client_category (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE ONLY client ADD CONSTRAINT client_org_id_fkey FOREIGN KEY (org_id) REFERENCES org (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE ONLY client ADD CONSTRAINT client_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES client (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE ONLY client ADD CONSTRAINT client_controller_id_fkey FOREIGN KEY (controller_id) REFERENCES client (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Table of feeds --
CREATE TABLE feed (
    id serial,
    description character varying(255) NOT NULL
);
ALTER TABLE feed OWNER TO togo;

ALTER TABLE ONLY feed ADD CONSTRAINT feed_id_pkey PRIMARY KEY (id);

-- Table of feeds for clients --
CREATE TABLE feeds_clients (
    feed_id integer NOT NULL,
    client_id integer NOT NULL
);
ALTER TABLE feeds_clients OWNER TO togo;

ALTER TABLE ONLY feeds_clients ADD CONSTRAINT feeds_clients_feed_id_fkey FOREIGN KEY (feed_id) REFERENCES feed (id);
ALTER TABLE ONLY feeds_clients ADD CONSTRAINT feeds_clients_client_id_fkey FOREIGN KEY (client_id) REFERENCES client (id);

-- Table of drains --
CREATE TABLE drain (
    id serial,
    name character varying(255) NOT NULL ,
    feed_id integer NOT NULL,
    unit_of_measure character varying(255),
    measure_id character varying(255),
    type character varying(20),
    decimals integer,
    base_drain_id integer,
    coefficient real,
    measure_type character varying(2) NOT NULL,
    client_default_drain boolean NOT NULL DEFAULT FALSE,
    diff_drain_id integer,
    positive_negative_value boolean NOT NULL DEFAULT FALSE,
    max_value double precision,
    min_value double precision
);
ALTER TABLE drain OWNER TO togo;

ALTER TABLE ONLY drain ADD CONSTRAINT drain_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY drain ADD CONSTRAINT drain_feedId_fkey FOREIGN KEY (feed_id) REFERENCES feed (id);
ALTER TABLE ONLY drain ADD CONSTRAINT drain_base_drain_fkey FOREIGN KEY (base_drain_id) REFERENCES drain (id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY drain ADD CONSTRAINT drain_diff_drain_fkey FOREIGN KEY (diff_drain_id) REFERENCES drain (id) ON UPDATE CASCADE ON DELETE CASCADE;

-- Table of double precision measures --
CREATE TABLE measure_double (
    drain_id integer NOT NULL,
    value double precision NOT NULL,
    "time" timestamp with time zone NOT NULL
);
ALTER TABLE measure_double OWNER TO togo;

ALTER TABLE ONLY measure_double ADD CONSTRAINT measure_double_pkey PRIMARY KEY (drain_id, "time");
ALTER TABLE ONLY measure_double ADD CONSTRAINT measure_double_drain_id_fkey FOREIGN KEY (drain_id) REFERENCES drain (id);

-- Table of string measures --
CREATE TABLE measure_string (
    drain_id integer NOT NULL,
    value character varying(255) NOT NULL,
    "time" timestamp with time zone NOT NULL
);
ALTER TABLE measure_string OWNER TO togo;

ALTER TABLE ONLY measure_string ADD CONSTRAINT measure_string_pkey PRIMARY KEY (drain_id, "time");
ALTER TABLE ONLY measure_string ADD CONSTRAINT measure_string_drain_id_fkey FOREIGN KEY (drain_id) REFERENCES drain (id);

-- Table of bitfield measures --
CREATE TABLE measure_bitfield (
    drain_id integer NOT NULL,
    value character varying(8) NOT NULL,
    "time" timestamp with time zone NOT NULL
);
ALTER TABLE public.measure_bitfield OWNER TO togo;

ALTER TABLE ONLY measure_bitfield ADD CONSTRAINT measure_bitfield_pkey PRIMARY KEY (drain_id, "time");
ALTER TABLE ONLY measure_bitfield ADD CONSTRAINT measure_bitfield_drain_id_fkey FOREIGN KEY (drain_id) REFERENCES drain (id);

-- Table of formulas --
CREATE TABLE formula (
    id serial,
    name character varying(255),
    org_id integer,
    client_id integer
);
ALTER TABLE formula OWNER TO togo;

ALTER TABLE ONLY formula ADD CONSTRAINT formula_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY formula ADD CONSTRAINT formula_org_id_fkey FOREIGN KEY (org_id) REFERENCES org (id);
ALTER TABLE ONLY formula ADD CONSTRAINT formula_client_id_fkey FOREIGN KEY (client_id) REFERENCES client (id);

-- Table of components of formulas --
CREATE TABLE formula_component (
    id serial,
    drain_id integer,
    aggregation character varying(5),
    formula_id integer NOT NULL,
    operator character varying(20),
    legend character varying(255),
    positive_negative_value varchar(10),
    exclude_outliers boolean DEFAULT FALSE
);
ALTER TABLE formula_component OWNER TO togo;

ALTER TABLE ONLY formula_component ADD CONSTRAINT formula_component_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY formula_component ADD CONSTRAINT formula_component_formula_id_fkey FOREIGN KEY (formula_id) REFERENCES formula (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY formula_component ADD CONSTRAINT formula_component_drain_id_fkey FOREIGN KEY (drain_id) REFERENCES drain (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Table of index groups --
CREATE TABLE index_group (
    id serial,
    name character varying(255) NOT NULL,
    org_id integer NOT NULL
);
ALTER TABLE index_group OWNER TO togo;

ALTER TABLE ONLY index_group ADD CONSTRAINT index_group_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY index_group ADD CONSTRAINT index_group_org_id_fkey FOREIGN KEY (org_id) REFERENCES org (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

-- Table of indices --
CREATE TABLE index (
    id serial,
    name character varying(255) NOT NULL,
    org_id integer NOT NULL,
    index_group_id integer,
    coefficient real,
    unit_of_measure character varying(255),
    decimals integer,
    min_value double precision,
    max_value double precision,
    warning_value double precision,
    alarm_value double precision
);
ALTER TABLE index OWNER TO togo;

ALTER TABLE ONLY index ADD CONSTRAINT index_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY index ADD CONSTRAINT index_index_group_id_fkey FOREIGN KEY (index_group_id) REFERENCES index_group (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Table of indices components --
CREATE TABLE index_component (
    id serial,
    formula_id integer NOT NULL,
    operator character varying(20),
    n_skip integer,
    skip_period character varying(3),
    index_id integer NOT NULL
);
ALTER TABLE index_component OWNER TO togo;

ALTER TABLE ONLY index_component ADD CONSTRAINT index_component_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY index_component ADD CONSTRAINT index_component_formula_id_fkey FOREIGN KEY (formula_id) REFERENCES formula (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY index_component ADD CONSTRAINT index_component_index_id_fkey FOREIGN KEY (index_id) REFERENCES index (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

-- Table of periodic check on measures received --
CREATE TABLE drain_control (
    id serial,
    name text NOT NULL,
    org_id integer NOT NULL,
    type character varying(50) NOT NULL,
    cron_second character varying(500) NOT NULL,
    cron_minute character varying(500) NOT NULL,
    cron_hour character varying(500) NOT NULL,
    cron_day_month character varying(500) NOT NULL,
    cron_day_week character varying(500) NOT NULL,
    cron_month character varying(500) NOT NULL,
    mail_receivers text NOT NULL,
    errors integer,
    last_mail_sent_time timestamp with time zone
);
ALTER TABLE drain_control OWNER TO togo;

ALTER TABLE ONLY drain_control ADD CONSTRAINT drain_control_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY drain_control ADD CONSTRAINT drain_control_org_id_fkey FOREIGN KEY (org_id) REFERENCES org (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE drain_control_detail (
    id serial,
    drain_control_id integer NOT NULL,
    drain_id integer,
    formula_id integer,
    last_minutes integer,
    aggregation character varying(5),
    low_threshold real,
    high_threshold real,
    delta real,
    active boolean NOT NULL DEFAULT FALSE,
    waiting_measures integer,
    error boolean NOT NULL DEFAULT FALSE,
    last_error_time timestamp with time zone
);
ALTER TABLE drain_control_detail OWNER TO togo;

ALTER TABLE ONLY drain_control_detail ADD CONSTRAINT drain_control_detail_id_pkey PRIMARY KEY (id);

ALTER TABLE ONLY drain_control_detail ADD CONSTRAINT drain_control_detail_drain_control_id_fkey FOREIGN KEY (drain_control_id) REFERENCES drain_control (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY drain_control_detail ADD CONSTRAINT drain_control_detail_drain_id_fkey FOREIGN KEY (drain_id) REFERENCES drain (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY drain_control_detail ADD CONSTRAINT drain_control_detail_formula_id_fkey FOREIGN KEY (formula_id) REFERENCES formula (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

-- Table of dashboards --
CREATE TABLE dashboard (
    id serial,
    name character varying(255) NOT NULL,
    org_id integer NOT NULL
);
ALTER TABLE dashboard OWNER TO togo;

ALTER TABLE ONLY dashboard ADD CONSTRAINT dashboard_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY dashboard ADD CONSTRAINT dashboard_name_ukey UNIQUE (name);
ALTER TABLE ONLY dashboard ADD CONSTRAINT dashboard_org_id_pkey FOREIGN KEY (org_id) REFERENCES org (id);

-- Table of dashboards users --
CREATE TABLE dashboards_users (
    dashboard_id integer NOT NULL,
    user_id integer NOT NULL,
    default_dashboard boolean DEFAULT false
);
ALTER TABLE dashboards_users OWNER TO togo;

ALTER TABLE ONLY dashboards_users ADD CONSTRAINT dashboards_users_dashboard_id_fkey FOREIGN KEY (dashboard_id) REFERENCES dashboard (id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY dashboards_users ADD CONSTRAINT dashboards_users_user_id_fkey FOREIGN KEY (user_id) REFERENCES application_user (id) ON UPDATE CASCADE ON DELETE CASCADE;

-- Table of dashboard widgets --
CREATE TABLE dashboard_widget (
    id serial,
    dashboard_id integer NOT NULL,
    n_cols integer NOT NULL,
    n_rows integer NOT NULL,
    x_pos integer NOT NULL,
    y_pos integer NOT NULL,
    widget_type character varying(20) NOT NULL,
    costs_drain_id integer,
    costs_aggregation character varying(5),
    interval_seconds integer,
    title text,
    background_color character varying(7),
    number_periods integer NOT NULL,
    period character varying(10) NOT NULL DEFAULT 'hours',
    start_time timestamp with time zone,
    end_time timestamp with time zone,
    legend boolean,
    legend_position character varying(20),
    legend_layout character varying(1),
    navigator boolean,
    time_aggregation character varying(10) NOT NULL DEFAULT 'ALL',
    min_value double precision,
    max_value double precision,
    warning_value double precision,
    alarm_value double precision,
    color1 character varying(7),
    color2 character varying(7),
    color3 character varying(7)
);
ALTER TABLE dashboard_widget OWNER TO togo;

ALTER TABLE ONLY dashboard_widget ADD CONSTRAINT dashboard_widget_id_pkey PRIMARY KEY (id);
ALTER TABLE ONLY dashboard_widget ADD CONSTRAINT dashboard_widget_dashboard_id_fkey FOREIGN KEY (dashboard_id) REFERENCES dashboard (id);
ALTER TABLE ONLY dashboard_widget ADD CONSTRAINT dashboard_widget_cdrain_id_fkey FOREIGN KEY (costs_drain_id) REFERENCES drain (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

-- Table of dashboard widget details --
CREATE TABLE dashboard_widget_detail (
    id serial,
    dashboard_widget_id integer NOT NULL,
    index_id integer,
    drain_id integer,
    formula_id integer,
    drain_control_id integer,
    aggregation character varying(5),
    operator character varying(20),
    positive_negative_value varchar(10),
    exclude_outliers boolean DEFAULT FALSE
);
ALTER TABLE dashboard_widget_detail OWNER TO togo;

ALTER TABLE ONLY dashboard_widget_detail ADD CONSTRAINT dashboard_widget_detail_id_pkey PRIMARY KEY (id);

ALTER TABLE ONLY dashboard_widget_detail ADD CONSTRAINT dashboard_widget_detail_dashboard_widget_id_fkey FOREIGN KEY (dashboard_widget_id) REFERENCES dashboard_widget (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY dashboard_widget_detail ADD CONSTRAINT dashboard_widget_detail_index_id_fkey FOREIGN KEY (index_id) REFERENCES index (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE ONLY dashboard_widget_detail ADD CONSTRAINT dashboard_widget_detail_drain_id_fkey FOREIGN KEY (drain_id) REFERENCES drain (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE ONLY dashboard_widget_detail ADD CONSTRAINT dashboard_widget_detail_formula_id_fkey FOREIGN KEY (formula_id) REFERENCES formula (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE ONLY dashboard_widget_detail ADD CONSTRAINT dashboard_widget_detail_drain_control_id_fkey FOREIGN KEY (drain_control_id) REFERENCES drain_control (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Table of vendors --
CREATE TABLE vendor (
    id serial,
    name character varying(100)
);
ALTER TABLE vendor OWNER TO togo;

ALTER TABLE vendor ADD CONSTRAINT vendor_id_pkey PRIMARY KEY (id);

-- Table of time slots --
CREATE TABLE time_slot (
    id serial,
    name character varying(100)
);
ALTER TABLE time_slot OWNER TO togo;

ALTER TABLE time_slot ADD CONSTRAINT time_slot_id_pkey PRIMARY KEY (id);

-- Time slots defined by Authority --
INSERT INTO time_slot (name) VALUES ('F1'), ('F2'), ('F3');

-- Table of time slot details --
CREATE TABLE time_slot_detail (
    id serial,
    time_slot_id integer NOT NULL,
    day_of_week integer NOT NULL,
    start_time time without time zone NOT NULL,
    end_time time without time zone NOT NULL
);
ALTER TABLE time_slot_detail OWNER TO togo;

ALTER TABLE time_slot_detail ADD CONSTRAINT time_slot_detail_id_pkey PRIMARY KEY (id);
ALTER TABLE time_slot_detail ADD CONSTRAINT time_slot_detail_time_slot_id_fkey FOREIGN KEY (time_slot_id) REFERENCES time_slot (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

-- Time slot details defined by Authority --
INSERT INTO time_slot_detail (time_slot_id, day_of_week, start_time, end_time) VALUES
(1, 1, '08:00:00', '18:59:59'), -- Mon
(1, 2, '08:00:00', '18:59:59'), -- Tue
(1, 3, '08:00:00', '18:59:59'), -- Wed
(1, 4, '08:00:00', '18:59:59'), -- Thu
(1, 5, '08:00:00', '18:59:59'), -- Fri
(2, 1, '07:00:00', '07:59:59'), -- Mon
(2, 1, '19:00:00', '22:59:59'), -- Mon
(2, 2, '07:00:00', '07:59:59'), -- Tue
(2, 2, '19:00:00', '22:59:59'), -- Tue
(2, 3, '07:00:00', '07:59:59'), -- Wed
(2, 3, '19:00:00', '22:59:59'), -- Wed
(2, 4, '07:00:00', '07:59:59'), -- Thu
(2, 4, '19:00:00', '22:59:59'), -- Thu
(2, 5, '07:00:00', '07:59:59'), -- Fri
(2, 5, '19:00:00', '22:59:59'), -- Fri
(2, 6, '07:00:00', '22:59:59'), -- Sat
(3, 0, '00:00:00', '23:59:59'), -- Sun (and holiday)
(3, 1, '00:00:00', '06:59:59'), -- Mon
(3, 1, '23:00:00', '23:59:59'), -- Mon
(3, 2, '00:00:00', '06:59:59'), -- Tue
(3, 2, '23:00:00', '23:59:59'), -- Tue
(3, 3, '00:00:00', '06:59:59'), -- Wed
(3, 3, '23:00:00', '23:59:59'), -- Wed
(3, 4, '00:00:00', '06:59:59'), -- Thu
(3, 4, '23:00:00', '23:59:59'), -- Thu
(3, 5, '00:00:00', '06:59:59'), -- Fri
(3, 5, '23:00:00', '23:59:59'), -- Fri
(3, 6, '00:00:00', '06:59:59'), -- Sat
(3, 6, '23:00:00', '23:59:59'); -- Sat

-- Table of invoice's items applied on kWh --
CREATE TABLE invoice_item_kwh (
    id serial NOT NULL,
    vendor_id integer NOT NULL,
    drain_id integer NOT NULL,
    year integer NOT NULL,
    month integer NOT NULL,
    f1_energy real NOT NULL, -- unit cost for kWh in time slot F1 (with discounts if any)
    f2_energy real NOT NULL, -- unit cost for kWh in time slot F2 (with discounts if any)
    f3_energy real NOT NULL, -- unit cost for kWh in time slot F3 (with discounts if any)
    interruptibility_remuneration real,
    production_capacity_availability real,
    grtn_operating_costs real,
    procurement_dispatching_resources real,
    reintegration_temporary_safeguard real,
    f1_unit_safety_costs real, -- unit cost for kWh in time slot F1
    f2_unit_safety_costs real, -- unit cost for kWh in time slot F2
    f3_unit_safety_costs real, -- unit cost for kWh in time slot F3
    transport_energy real,
    transport_energy_equalization real,
    system_charges_energy real,
    duty_excise_1 real, -- unit cost for first 200000 kWh
    duty_excise_2 real, -- unit cost for kWh over 200000 kWh if total kWh is less than 1200000
    duty_excise_3 real, -- fixed cost if total kWh is more than 1200000
    f1_reactive_energy_33 real, -- unit cost for reactive energy in F1 from 33 to 75 percent
    f2_reactive_energy_33 real, -- unit cost for reactive energy in F2 greater than 75 percent
    f3_reactive_energy_33 real, -- unit cost for reactive energy in F3 greater than 75 percent
    f1_reactive_energy_75 real, -- unit cost for reactive energy in F1 from 33 to 75 percent
    f2_reactive_energy_75 real, -- unit cost for reactive energy in F2 greater than 75 percent
    f3_reactive_energy_75 real, -- unit cost for reactive energy in F3 from 33 to 75 percent
    loss_perc_rate real, -- percentage rate of kWh loss
    vat_perc_rate real -- VAT percentage applied on each item
);
ALTER TABLE invoice_item_kwh OWNER TO togo;

ALTER TABLE invoice_item_kwh ADD CONSTRAINT invoice_item_kwh_id_pkey PRIMARY KEY (id);
ALTER TABLE invoice_item_kwh ADD CONSTRAINT invoice_item_kwh_vendor_id_fkey FOREIGN KEY (vendor_id) REFERENCES vendor (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE invoice_item_kwh ADD CONSTRAINT invoice_item_kwh_drain_id_fkey FOREIGN KEY (drain_id) REFERENCES drain (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

-- Table of Business Intelligence jobs --
CREATE TABLE bi_job (
    id serial,
    type character varying(50),
    state character varying(50),
    params character varying(200),
    result character varying(200)
);
ALTER TABLE bi_job OWNER TO togo;

ALTER TABLE bi_job ADD CONSTRAINT bi_job_id_pkey PRIMARY KEY (id);

-- Table of WEMO's sensor data --
CREATE TABLE wemo_sensordata (
    id serial,
    deviceid character varying(50) NOT NULL,
    datetime timestamp without time zone NOT NULL,
    "timestamp" character varying(50) NOT NULL,
    exercise integer NOT NULL,
    repetition integer NOT NULL,
    phase integer NOT NULL,
    accx real NOT NULL,
    accy real NOT NULL,
    accz real NOT NULL,
    gyrox real NOT NULL,
    gyroy real NOT NULL,
    gyroz real NOT NULL,
    magx real NOT NULL,
    magy real NOT NULL,
    magz real NOT NULL,
    emg1 real NOT NULL,
    emg2 real NOT NULL,
    emg3 real NOT NULL,
    emg4 real NOT NULL,
    emg5 real NOT NULL,
    emg6 real NOT NULL,
    emg7 real NOT NULL,
    emg8 real NOT NULL,
    normalizedval double precision NOT NULL
);
ALTER TABLE wemo_sensordata OWNER TO togo;

ALTER TABLE ONLY wemo_sensordata ADD CONSTRAINT wemo_sensordata_id_pkey PRIMARY KEY (id);

-- Table of WEMO's cmc values --
CREATE TABLE wemo_cmc (
    id serial,
    deviceid character varying(50) NOT NULL,
    datetime timestamp without time zone NOT NULL,
    value double precision NOT NULL
);
ALTER TABLE wemo_cmc OWNER TO togo;

ALTER TABLE ONLY wemo_cmc ADD CONSTRAINT wemo_cmc_id_pkey PRIMARY KEY (id);

-- Table of WEMO's sessions --
CREATE TABLE wemo_session (
    deviceid character varying(50) NOT NULL,
    datetime timestamp without time zone NOT NULL,
    username character varying(50) NOT NULL,
    userquestionone integer NOT NULL,
    userquestiontwo integer NOT NULL
);
ALTER TABLE wemo_session OWNER TO togo;

ALTER TABLE ONLY wemo_session ADD CONSTRAINT wemo_session_pkey PRIMARY KEY (deviceid, datetime);

ALTER TABLE ONLY wemo_cmc ADD CONSTRAINT wemo_cmc_session_fkey FOREIGN KEY (deviceid, datetime) REFERENCES wemo_session (deviceid, datetime) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY wemo_sensordata ADD CONSTRAINT wemo_sensordata_session_fkey FOREIGN KEY (deviceid, datetime) REFERENCES wemo_session (deviceid, datetime) ON UPDATE CASCADE ON DELETE CASCADE;

-- Database indices --
CREATE INDEX client_org_id_parent_id ON client USING btree (org_id, parent_id);
CREATE INDEX client_parent_id ON client USING btree (parent_id);
CREATE INDEX drain_feed_id ON drain USING btree (feed_id);
CREATE INDEX feeds_clients_client_id ON feeds_clients USING btree (client_id);
CREATE INDEX feeds_clients_feed_id ON feeds_clients USING btree (feed_id);
CREATE INDEX org_id_parent_id ON org USING btree (id, parent_id);
CREATE INDEX drain_feed_id_name ON drain USING btree (feed_id, name COLLATE pg_catalog."default");
CREATE INDEX measure_bitfield_drain_id ON measure_bitfield USING btree (drain_id);
CREATE INDEX measure_double_drain_id ON measure_double USING btree (drain_id);
CREATE INDEX measure_string_drain_id ON measure_string USING btree (drain_id);
CREATE UNIQUE INDEX client_device_id_org_id_parent_id ON client (device_id, org_id, parent_id);
CREATE UNIQUE INDEX feeds_clients_feed_id_client_id ON feeds_clients (feed_id, client_id);

-- Database triggers --
CREATE OR REPLACE FUNCTION derived_drain() RETURNS TRIGGER AS $derived_measures$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        DELETE FROM measure_double WHERE drain_id IN (SELECT d.id FROM drain d WHERE d.base_drain_id = OLD.drain_id) AND "time" = OLD."time";
        IF NOT FOUND THEN RETURN NULL; END IF;
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE') THEN
        UPDATE measure_double SET value = NEW.value * (SELECT d.coefficient FROM drain d WHERE d.base_drain_id = NEW.drain_id) WHERE drain_id IN (SELECT d.id FROM drain d WHERE d.base_drain_id = OLD.drain_id) AND "time" = NEW."time";
        RETURN NEW;
    ELSIF (TG_OP = 'INSERT') THEN
        INSERT INTO measure_double (drain_id, value, "time") SELECT d.id, NEW.value * d.coefficient, NEW."time" FROM drain d WHERE d.base_drain_id = NEW.drain_id;
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$derived_measures$ LANGUAGE plpgsql;

CREATE TRIGGER derived_measures
    AFTER INSERT OR UPDATE OR DELETE ON measure_double
    FOR EACH ROW EXECUTE PROCEDURE derived_drain();

-- TimescaleDB Extension
CREATE EXTENSION IF NOT EXISTS timescaledb;

SELECT create_hypertable('measure_double', 'time', migrate_data => true);

CREATE MATERIALIZED VIEW measure_metrics_hourly WITH (timescaledb.continuous) AS SELECT drain_id, avg(value), max(value), min(value), sum(value), time_bucket('1 hours', time) AS bucket FROM measure_double GROUP BY drain_id, bucket WITH NO DATA;
