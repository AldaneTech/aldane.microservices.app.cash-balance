--
-- TOC entry 220 (class 1259 OID 16457)
-- Name: user; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public."user" (
    id integer NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    comment text,
    last_access timestamp without time zone,
    record_date timestamp without time zone,
    status_id integer,
    role_id integer
);



--
-- TOC entry 219 (class 1259 OID 16456)
-- Name: User_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public."User_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3457 (class 0 OID 0)
-- Dependencies: 219
-- Name: User_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public."User_id_seq" OWNED BY public."user".id;


--
-- TOC entry 232 (class 1259 OID 16555)
-- Name: account_entry; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.account_entry (
    id integer NOT NULL,
    amount double precision NOT NULL,
    comment text,
    date date NOT NULL,
    store_id integer,
    brand_id integer,
    status_id integer,
    category_id integer,
    wallet_id integer,
    type_operation_id integer
);

--
-- TOC entry 231 (class 1259 OID 16554)
-- Name: accountentry_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.accountentry_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- TOC entry 3458 (class 0 OID 0)
-- Dependencies: 231
-- Name: accountentry_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.accountentry_id_seq OWNED BY public.account_entry.id;


--
-- TOC entry 228 (class 1259 OID 16527)
-- Name: brand; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.brand (
    id integer NOT NULL,
    comment text,
    status_id integer,
    name character varying(255) NOT NULL,
    last_modification timestamp without time zone,
    user_id integer
);


--
-- TOC entry 227 (class 1259 OID 16526)
-- Name: brand_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.brand_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3459 (class 0 OID 0)
-- Dependencies: 227
-- Name: brand_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.brand_id_seq OWNED BY public.brand.id;


--
-- TOC entry 224 (class 1259 OID 16499)
-- Name: category; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.category (
    id integer NOT NULL,
    comment text,
    status_id integer,
    name character varying(255) NOT NULL,
    last_modification timestamp without time zone,
    user_id integer
);


--
-- TOC entry 223 (class 1259 OID 16498)
-- Name: category_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.category_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3460 (class 0 OID 0)
-- Dependencies: 223
-- Name: category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.category_id_seq OWNED BY public.category.id;


--
-- TOC entry 230 (class 1259 OID 16541)
-- Name: operation_type; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.operation_type (
    id integer NOT NULL,
    comment text,
    status_id integer,
    name character varying(255) NOT NULL,
    last_modification date
);


--
-- TOC entry 229 (class 1259 OID 16540)
-- Name: operationtype_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.operationtype_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3461 (class 0 OID 0)
-- Dependencies: 229
-- Name: operationtype_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.operationtype_id_seq OWNED BY public.operation_type.id;


--
-- TOC entry 218 (class 1259 OID 16443)
-- Name: role; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.role (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment text,
    status_id integer
);


--
-- TOC entry 217 (class 1259 OID 16442)
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3462 (class 0 OID 0)
-- Dependencies: 217
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.role_id_seq OWNED BY public.role.id;


--
-- TOC entry 216 (class 1259 OID 16436)
-- Name: status; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.status (
    id integer NOT NULL,
    name character varying(255) NOT NULL
);


--
-- TOC entry 215 (class 1259 OID 16435)
-- Name: status_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.status_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3463 (class 0 OID 0)
-- Dependencies: 215
-- Name: status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.status_id_seq OWNED BY public.status.id;


--
-- TOC entry 226 (class 1259 OID 16513)
-- Name: store; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.store (
    id integer NOT NULL,
    comment text,
    status_id integer,
    name character varying(255) NOT NULL,
    last_modification timestamp without time zone,
    user_id integer
);


--
-- TOC entry 225 (class 1259 OID 16512)
-- Name: store_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.store_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3464 (class 0 OID 0)
-- Dependencies: 225
-- Name: store_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.store_id_seq OWNED BY public.store.id;


--
-- TOC entry 222 (class 1259 OID 16480)
-- Name: wallet; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.wallet (
    id integer NOT NULL,
    budget double precision NOT NULL,
    comment text,
    status_id integer,
    name character varying(255) NOT NULL,
    user_id integer,
    last_modification timestamp without time zone,
    creation_date timestamp without time zone
);


--
-- TOC entry 221 (class 1259 OID 16479)
-- Name: wallet_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.wallet_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3465 (class 0 OID 0)
-- Dependencies: 221
-- Name: wallet_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.wallet_id_seq OWNED BY public.wallet.id;


--
-- TOC entry 3251 (class 2604 OID 16558)
-- Name: account_entry id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.account_entry ALTER COLUMN id SET DEFAULT nextval('public.accountentry_id_seq'::regclass);


--
-- TOC entry 3249 (class 2604 OID 16530)
-- Name: brand id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.brand ALTER COLUMN id SET DEFAULT nextval('public.brand_id_seq'::regclass);


--
-- TOC entry 3247 (class 2604 OID 16502)
-- Name: category id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.category ALTER COLUMN id SET DEFAULT nextval('public.category_id_seq'::regclass);


--
-- TOC entry 3250 (class 2604 OID 16544)
-- Name: operation_type id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.operation_type ALTER COLUMN id SET DEFAULT nextval('public.operationtype_id_seq'::regclass);


--
-- TOC entry 3244 (class 2604 OID 16446)
-- Name: role id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.role ALTER COLUMN id SET DEFAULT nextval('public.role_id_seq'::regclass);


--
-- TOC entry 3243 (class 2604 OID 16439)
-- Name: status id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.status ALTER COLUMN id SET DEFAULT nextval('public.status_id_seq'::regclass);


--
-- TOC entry 3248 (class 2604 OID 16516)
-- Name: store id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.store ALTER COLUMN id SET DEFAULT nextval('public.store_id_seq'::regclass);


--
-- TOC entry 3245 (class 2604 OID 16460)
-- Name: user id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public."user" ALTER COLUMN id SET DEFAULT nextval('public."User_id_seq"'::regclass);


--
-- TOC entry 3246 (class 2604 OID 16483)
-- Name: wallet id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.wallet ALTER COLUMN id SET DEFAULT nextval('public.wallet_id_seq'::regclass);


--
-- TOC entry 3447 (class 0 OID 16541)
-- Dependencies: 230
-- Data for Name: operation_type; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.operation_type VALUES (1, 'Tipo para los ingresos', 1, 'Ingreso', '2024-08-23');
INSERT INTO public.operation_type VALUES (3, 'Tipo para las inversiones', 1, 'Inversión', '2024-08-23');
INSERT INTO public.operation_type VALUES (2, 'Tipo para los gastos', 1, 'Gasto', '2024-08-23');


--
-- TOC entry 3435 (class 0 OID 16443)
-- Dependencies: 218
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.role VALUES (2, 'USER', 'Role for common user', 1);
INSERT INTO public.role VALUES (1, 'ADMIN', 'Role for admin user', 1);


--
-- TOC entry 3433 (class 0 OID 16436)
-- Dependencies: 216
-- Data for Name: status; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.status VALUES (1, 'Activo');
INSERT INTO public.status VALUES (2, 'Inactivo');



--
-- TOC entry 3470 (class 0 OID 0)
-- Dependencies: 229
-- Name: operationtype_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.operationtype_id_seq', 4, true);


--
-- TOC entry 3471 (class 0 OID 0)
-- Dependencies: 217
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.role_id_seq', 3, true);


--
-- TOC entry 3472 (class 0 OID 0)
-- Dependencies: 215
-- Name: status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.status_id_seq', 3, true);


--
-- TOC entry 3257 (class 2606 OID 16468)
-- Name: user User_email_key; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT "User_email_key" UNIQUE (email);


--
-- TOC entry 3259 (class 2606 OID 16464)
-- Name: user User_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT "User_pkey" PRIMARY KEY (id);


--
-- TOC entry 3261 (class 2606 OID 16466)
-- Name: user User_username_key; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT "User_username_key" UNIQUE (username);


--
-- TOC entry 3273 (class 2606 OID 16562)
-- Name: account_entry accountentry_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.account_entry
    ADD CONSTRAINT accountentry_pkey PRIMARY KEY (id);


--
-- TOC entry 3269 (class 2606 OID 16534)
-- Name: brand brand_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.brand
    ADD CONSTRAINT brand_pkey PRIMARY KEY (id);


--
-- TOC entry 3265 (class 2606 OID 16506)
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- TOC entry 3271 (class 2606 OID 16548)
-- Name: operation_type operationtype_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.operation_type
    ADD CONSTRAINT operationtype_pkey PRIMARY KEY (id);


--
-- TOC entry 3255 (class 2606 OID 16450)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 3253 (class 2606 OID 16441)
-- Name: status status_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.status
    ADD CONSTRAINT status_pkey PRIMARY KEY (id);


--
-- TOC entry 3267 (class 2606 OID 16520)
-- Name: store store_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.store
    ADD CONSTRAINT store_pkey PRIMARY KEY (id);


--
-- TOC entry 3263 (class 2606 OID 16487)
-- Name: wallet wallet_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.wallet
    ADD CONSTRAINT wallet_pkey PRIMARY KEY (id);


--
-- TOC entry 3275 (class 2606 OID 16474)
-- Name: user User_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT "User_role_id_fkey" FOREIGN KEY (role_id) REFERENCES public.role(id);


--
-- TOC entry 3276 (class 2606 OID 16469)
-- Name: user User_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT "User_status_id_fkey" FOREIGN KEY (status_id) REFERENCES public.status(id);


--
-- TOC entry 3283 (class 2606 OID 16578)
-- Name: account_entry accountentry_brand_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.account_entry
    ADD CONSTRAINT accountentry_brand_id_fkey FOREIGN KEY (brand_id) REFERENCES public.brand(id);


--
-- TOC entry 3284 (class 2606 OID 16568)
-- Name: account_entry accountentry_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.account_entry
    ADD CONSTRAINT accountentry_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.category(id);


--
-- TOC entry 3285 (class 2606 OID 16573)
-- Name: account_entry accountentry_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.account_entry
    ADD CONSTRAINT accountentry_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(id);


--
-- TOC entry 3286 (class 2606 OID 16583)
-- Name: account_entry accountentry_store_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.account_entry
    ADD CONSTRAINT accountentry_store_id_fkey FOREIGN KEY (store_id) REFERENCES public.store(id);


--
-- TOC entry 3287 (class 2606 OID 16588)
-- Name: account_entry accountentry_type_operation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.account_entry
    ADD CONSTRAINT accountentry_type_operation_id_fkey FOREIGN KEY (type_operation_id) REFERENCES public.operation_type(id);


--
-- TOC entry 3288 (class 2606 OID 16563)
-- Name: account_entry accountentry_wallet_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.account_entry
    ADD CONSTRAINT accountentry_wallet_id_fkey FOREIGN KEY (wallet_id) REFERENCES public.wallet(id);


--
-- TOC entry 3281 (class 2606 OID 16535)
-- Name: brand brand_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.brand
    ADD CONSTRAINT brand_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(id);

ALTER TABLE ONLY public.brand
    ADD CONSTRAINT brand_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user(id);
--
-- TOC entry 3279 (class 2606 OID 16507)
-- Name: category category_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(id);

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user(id);
--
-- TOC entry 3282 (class 2606 OID 16549)
-- Name: operation_type operationtype_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.operation_type
    ADD CONSTRAINT operationtype_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(id);


--
-- TOC entry 3274 (class 2606 OID 16451)
-- Name: role role_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(id);


--
-- TOC entry 3280 (class 2606 OID 16521)
-- Name: store store_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.store
    ADD CONSTRAINT store_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(id);

ALTER TABLE ONLY public.store
    ADD CONSTRAINT store_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user(id);
--
-- TOC entry 3277 (class 2606 OID 16488)
-- Name: wallet wallet_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.wallet
    ADD CONSTRAINT wallet_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(id);


--
-- TOC entry 3278 (class 2606 OID 16493)
-- Name: wallet wallet_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.wallet
    ADD CONSTRAINT wallet_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(id);


-- Completed on 2025-03-09 22:41:02 CET

--
-- PostgreSQL database dump complete
--
