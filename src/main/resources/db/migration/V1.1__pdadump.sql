--
-- Name: inventory; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS inventory (
    inventory_id character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    operating_system_id integer NOT NULL,
    service_type_id integer NOT NULL,
    who_create character varying(255),
    who_update character varying(255),
    created_at timestamp(0) without time zone,
    updated_at timestamp(0) without time zone,
    deleted_at timestamp(0) without time zone
);


--
-- Name: messages; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS messages  (
    id integer NOT NULL,
    message character varying(255) NOT NULL,
    bg_color character varying(255) NOT NULL,
    color character varying(255) NOT NULL,
    starts_at timestamp(0) without time zone NOT NULL,
    ends_at timestamp(0) without time zone NOT NULL,
    status boolean NOT NULL
);


--
-- Name: messages_id_seq; Type: SEQUENCE; Schema: public; Owner: pda
--

CREATE SEQUENCE IF NOT EXISTS messages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pda
--


--
-- Name: migrations; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS migrations  (
    id integer NOT NULL,
    migration character varying(255) NOT NULL,
    batch integer NOT NULL
);




--
-- Name: migrations_id_seq; Type: SEQUENCE; Schema: public; Owner: pda
--

CREATE SEQUENCE IF NOT EXISTS migrations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: migrations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pda
--

ALTER SEQUENCE migrations_id_seq OWNED BY migrations.id;


--
-- Name: operating_systems; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS operating_systems (
    id integer NOT NULL,
    operating_system_name character varying(255),
    operating_system_type character varying(255) NOT NULL,
    operating_system_logo character varying(255),
    operating_system_type_logo character varying(255) NOT NULL
);


--
-- Name: operating_systems_id_seq; Type: SEQUENCE; Schema: public; Owner: pda
--

CREATE SEQUENCE IF NOT EXISTS operating_systems_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: operating_systems_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pda
--

--
-- Name: password_resets; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS password_resets (
    email character varying(255) NOT NULL,
    token character varying(255) NOT NULL,
    created_at timestamp(0) without time zone
);


--
-- Name: person2; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS person2 (
    id integer,
    name character varying(100),
    surname character varying(100),
    address character varying(100)
);


--
-- Name: plogs; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS plogs (
    session_id character varying(255) NOT NULL,
    action_time timestamp(0) without time zone NOT NULL,
    proxy_action text NOT NULL
);


--
-- Name: policy; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS policy (
    id integer NOT NULL,
    regex character varying(255) NOT NULL,
    status boolean NOT NULL,
    who_create character varying(255) NOT NULL,
    created_at timestamp(0) without time zone,
    updated_at timestamp(0) without time zone
);


--
-- Name: policy_id_seq; Type: SEQUENCE; Schema: public; Owner: pda
--

CREATE SEQUENCE IF NOT EXISTS policy_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: policy_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pda
--

--
-- Name: psessions; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS psessions (
    session_id character varying(255) NOT NULL,
    external_session_id character varying(255) NOT NULL,
    username character varying(20) NOT NULL,
    start_time timestamp(0) without time zone NOT NULL,
    end_time timestamp(0) without time zone,
    session_type character varying(255) NOT NULL,
    inventory_id character varying(255)
);

--
-- Name: service_types; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS service_types (
    id integer NOT NULL,
    service_type_name character varying(255) NOT NULL,
    service_type character varying(10) NOT NULL,
    service_type_default_port integer NOT NULL,
    service_type_icon character varying(255)
);


--
-- Name: service_types_id_seq; Type: SEQUENCE; Schema: public; Owner: pda
--

CREATE SEQUENCE IF NOT EXISTS service_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: service_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pda
--


--
-- Name: sessions; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS sessions (
    id character varying(255) NOT NULL,
    user_name character varying(255),
    ip_address character varying(45),
    user_agent text,
    payload text NOT NULL,
    last_activity integer NOT NULL,
    created_at timestamp(0) without time zone,
    updated_at timestamp(0) without time zone
);


--
-- Name: system_settings; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS system_settings (
    id integer NOT NULL,
    setting_tag character varying(255) NOT NULL,
    setting_value text,
    setting_short_code character varying(255),
    setting_category character varying(255),
    created_at timestamp(0) without time zone,
    updated_at timestamp(0) without time zone
);


--
-- Name: system_settings_id_seq; Type: SEQUENCE; Schema: public; Owner: pda
--

CREATE SEQUENCE IF NOT EXISTS system_settings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: system_settings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pda
--


--
-- Name: tenant; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS tenant (
    tenant_id character varying(255) NOT NULL,
    company_name character varying(255) NOT NULL,
    country character varying(255) NOT NULL,
    created_at timestamp(0) without time zone,
    updated_at timestamp(0) without time zone
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS users (
    user_id character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    remember_token character varying(100),
    created_at timestamp(0) without time zone,
    updated_at timestamp(0) without time zone,
    company character varying(255) DEFAULT ''::character varying NOT NULL,
    phone character varying(255) DEFAULT ''::character varying NOT NULL,
    tenant_id character varying(255) NOT NULL,
    urole character varying(255) DEFAULT 'user'::character varying NOT NULL
);


--
-- Name: users_inventory; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS users_inventory (
    id integer NOT NULL,
    user_id character varying(255) NOT NULL,
    inventory_id character varying(255) NOT NULL
);


--
-- Name: users_inventory_id_seq; Type: SEQUENCE; Schema: public; Owner: pda
--

CREATE SEQUENCE IF NOT EXISTS users_inventory_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_inventory_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pda
--




--
-- Name: vlogs; Type: TABLE; Schema: public; Owner: pda
--

CREATE TABLE IF NOT EXISTS vlogs (
    id character varying(255) NOT NULL,
    action_time timestamp(0) without time zone NOT NULL,
    vault_action text NOT NULL
);


--
-- Name: messages id; Type: DEFAULT; Schema: public; Owner: pda
--

ALTER TABLE ONLY messages ALTER COLUMN id SET DEFAULT nextval('messages_id_seq'::regclass);


--
-- Name: migrations id; Type: DEFAULT; Schema: public; Owner: pda
--

ALTER TABLE ONLY migrations ALTER COLUMN id SET DEFAULT nextval('migrations_id_seq'::regclass);


--
-- Name: operating_systems id; Type: DEFAULT; Schema: public; Owner: pda
--

ALTER TABLE ONLY operating_systems ALTER COLUMN id SET DEFAULT nextval('operating_systems_id_seq'::regclass);


--
-- Name: policy id; Type: DEFAULT; Schema: public; Owner: pda
--

ALTER TABLE ONLY policy ALTER COLUMN id SET DEFAULT nextval('policy_id_seq'::regclass);


--
-- Name: service_types id; Type: DEFAULT; Schema: public; Owner: pda
--

ALTER TABLE ONLY service_types ALTER COLUMN id SET DEFAULT nextval('service_types_id_seq'::regclass);


--
-- Name: system_settings id; Type: DEFAULT; Schema: public; Owner: pda
--

ALTER TABLE ONLY system_settings ALTER COLUMN id SET DEFAULT nextval('system_settings_id_seq'::regclass);


--
-- Name: users_inventory id; Type: DEFAULT; Schema: public; Owner: pda
--

ALTER TABLE ONLY users_inventory ALTER COLUMN id SET DEFAULT nextval('users_inventory_id_seq'::regclass);


----
---- Name: inventory inventory_pkey; Type: CONSTRAINT; Schema: public; Owner: pda
----
--
ALTER TABLE ONLY inventory
    ADD CONSTRAINT inventory_pkey PRIMARY KEY (inventory_id);


--
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- Name: migrations migrations_pkey; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY migrations
    ADD CONSTRAINT migrations_pkey PRIMARY KEY (id);


--
-- Name: operating_systems operating_systems_pkey; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY operating_systems
    ADD CONSTRAINT operating_systems_pkey PRIMARY KEY (id);

--
-- Name: policy policy_pkey; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY policy
    ADD CONSTRAINT policy_pkey PRIMARY KEY (id);


--
-- Name: psessions psessions_pkey; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY psessions
    ADD CONSTRAINT psessions_pkey PRIMARY KEY (session_id);




--
-- Name: service_types service_types_pkey; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY service_types
    ADD CONSTRAINT service_types_pkey PRIMARY KEY (id);


--
-- Name: sessions sessions_id_unique; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY sessions
    ADD CONSTRAINT sessions_id_unique UNIQUE (id);


--
-- Name: system_settings system_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY system_settings
    ADD CONSTRAINT system_settings_pkey PRIMARY KEY (id);


--
-- Name: tenant tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY tenant
    ADD CONSTRAINT tenant_pkey PRIMARY KEY (tenant_id);


--
-- Name: users users_email_unique; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_email_unique UNIQUE (email);


--
-- Name: users_inventory users_inventory_pkey; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY users_inventory
    ADD CONSTRAINT users_inventory_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);

--
-- Name: password_resets_email_index; Type: INDEX; Schema: public; Owner: pda
--

CREATE INDEX IF NOT EXISTS password_resets_email_index ON password_resets USING btree (email);

