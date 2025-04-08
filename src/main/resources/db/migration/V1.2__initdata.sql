--
-- Data for Name: inventory; Type: TABLE DATA; Schema: public; Owner: pda
--

---
--- COPY inventory FROM STDIN;
--- 123	Portal	1	1	671c4844-4201-3f3b-bafe-61202286d295	\N	2017-11-25 01:41:23	2017-11-25 01:41:23	\N
--- \.


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY messages FROM STDIN;
\.


--
-- Name: messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pda
--

select pg_catalog.setval('messages_id_seq', 4, true);


--
-- Data for Name: migrations; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY migrations FROM STDIN;
\.


--
-- Name: migrations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pda
--

select pg_catalog.setval('migrations_id_seq', 23, true);


--
-- Data for Name: operating_systems; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY operating_systems FROM STDIN;
1	Linux	linux	linux	linux
2	Unix	unix	unix	unix
3	RedHat	linux	redhat	linux
4	Ubuntu	linux	ubuntu	linux
5	CentOS	linux	centos	linux
6	Solaris	unix	solaris	unix
7	Debian	linux	debian	linux
8	Windows	windows	windows	windows
\.


--
-- Name: operating_systems_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pda
--

select pg_catalog.setval('operating_systems_id_seq', 48, true);


--
-- Data for Name: password_resets; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY password_resets FROM STDIN;
\.


--
-- Data for Name: plogs; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY plogs FROM STDIN;
\.


--
-- Data for Name: policy; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY policy FROM STDIN;
\.


--
-- Name: policy_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pda
--

select pg_catalog.setval('policy_id_seq', 14, true);


--
-- Data for Name: psessions; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY psessions FROM STDIN;
\.


--
-- Data for Name: service_types; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY service_types (id, service_type_name, service_type, service_type_default_port, service_type_icon) FROM STDIN;
1	MySQL	D	1234	mysql
2	PostgreSQL	D	5432	postgresql
3	MsSQL	D	1433	msserver
4	Oracle	D	1521	oracle
5	SSH	T	22	ssh
6	TELNET	T	23	ssh
7	RDP	T	3389	ssh
8	VNC	T	5900	ssh
\.


--
-- Name: service_types_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pda
--

select pg_catalog.setval('service_types_id_seq', 48, true);


--
-- Data for Name: sessions; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY sessions (id, user_name, ip_address, user_agent, payload, last_activity, created_at, updated_at) FROM STDIN;
\.


--
-- Data for Name: system_settings; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY system_settings (id, setting_tag, setting_value, setting_short_code, setting_category, created_at, updated_at) FROM STDIN;
5	registration_page_visibility	0	LFRPV	lookfeel	\N	\N
6	login_page_logo		LFLPL	lookfeel	\N	\N
7	login_page_message		LFLPM	lookfeel	\N	\N
1	default_system_host_name	\N	GSSHN	generalSettings	\N	2017-12-07 18:32:04
2	default_system_host_port	\N	GSSHP	generalSettings	\N	2017-12-07 18:32:04
3	default_system_proxy_name	\N	GSSPN	generalSettings	\N	2017-12-07 18:32:04
4	default_system_proxy_port	\N	GSSPP	generalSettings	\N	2017-12-07 18:32:04
8	two_factor_auth	1	AUTHTFA	auth	\N	2017-12-12 23:11:36
10	after_sign_out_path	www.h2hsecure.com	AUTHASOP	auth	\N	2017-12-12 23:11:48
9	home_page_url	www.h2hsecure.com	AUTHHPU	auth	\N	2017-12-12 23:11:36
\.


--
-- Name: system_settings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pda
--

select pg_catalog.setval('system_settings_id_seq', 10, true);


--
-- Data for Name: tenant; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY tenant FROM STDIN;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY users FROM STDIN;
\.


--
-- Data for Name: users_inventory; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY users_inventory FROM STDIN;
\.


--
-- Name: users_inventory_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pda
--

select pg_catalog.setval('users_inventory_id_seq', 43, true);


--
-- Data for Name: vlogs; Type: TABLE DATA; Schema: public; Owner: pda
--

COPY vlogs FROM STDIN;
\.
