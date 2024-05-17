--
-- PostgreSQL database dump
--

-- Dumped from database version 16.2 (Debian 16.2-1.pgdg120+2)
-- Dumped by pg_dump version 16.1

-- Started on 2024-05-17 13:15:56 UTC

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

ALTER TABLE IF EXISTS ONLY public.address DROP CONSTRAINT IF EXISTS "fk zip code";
ALTER TABLE IF EXISTS ONLY public.warehouse_product DROP CONSTRAINT IF EXISTS "fk warehouse id";
ALTER TABLE IF EXISTS ONLY public.orders DROP CONSTRAINT IF EXISTS "fk status code";
ALTER TABLE IF EXISTS ONLY public.product_specification DROP CONSTRAINT IF EXISTS "fk specification id";
ALTER TABLE IF EXISTS ONLY public.category_specification DROP CONSTRAINT IF EXISTS "fk specid";
ALTER TABLE IF EXISTS ONLY public.orders DROP CONSTRAINT IF EXISTS "fk shipment id";
ALTER TABLE IF EXISTS ONLY public.order_product DROP CONSTRAINT IF EXISTS "fk productid";
ALTER TABLE IF EXISTS ONLY public.warehouse_product DROP CONSTRAINT IF EXISTS "fk product id";
ALTER TABLE IF EXISTS ONLY public.product_specification DROP CONSTRAINT IF EXISTS "fk product id";
ALTER TABLE IF EXISTS ONLY public.product_image DROP CONSTRAINT IF EXISTS "fk product id";
ALTER TABLE IF EXISTS ONLY public.product_documentation DROP CONSTRAINT IF EXISTS "fk product id";
ALTER TABLE IF EXISTS ONLY public.product_component DROP CONSTRAINT IF EXISTS "fk product id";
ALTER TABLE IF EXISTS ONLY public.product_category DROP CONSTRAINT IF EXISTS "fk product id";
ALTER TABLE IF EXISTS ONLY public.order_product DROP CONSTRAINT IF EXISTS "fk orderid";
ALTER TABLE IF EXISTS ONLY public.product_image DROP CONSTRAINT IF EXISTS "fk img id";
ALTER TABLE IF EXISTS ONLY public.product_image DROP CONSTRAINT IF EXISTS "fk img downscaled id";
ALTER TABLE IF EXISTS ONLY public.orders DROP CONSTRAINT IF EXISTS "fk employee id";
ALTER TABLE IF EXISTS ONLY public.orders DROP CONSTRAINT IF EXISTS "fk customer id";
ALTER TABLE IF EXISTS ONLY public.product_component DROP CONSTRAINT IF EXISTS "fk component id";
ALTER TABLE IF EXISTS ONLY public.category_specification DROP CONSTRAINT IF EXISTS "fk catid";
ALTER TABLE IF EXISTS ONLY public.product_category DROP CONSTRAINT IF EXISTS "fk category id";
ALTER TABLE IF EXISTS ONLY public.users DROP CONSTRAINT IF EXISTS "fk address id";
ALTER TABLE IF EXISTS ONLY public.zip_code DROP CONSTRAINT IF EXISTS zip_code_pkey;
ALTER TABLE IF EXISTS ONLY public.warehouse_product DROP CONSTRAINT IF EXISTS warehouse_product_pkey;
ALTER TABLE IF EXISTS ONLY public.warehouse DROP CONSTRAINT IF EXISTS warehouse_pkey;
ALTER TABLE IF EXISTS ONLY public.users DROP CONSTRAINT IF EXISTS users_pkey;
ALTER TABLE IF EXISTS ONLY public.specification DROP CONSTRAINT IF EXISTS specification_pkey;
ALTER TABLE IF EXISTS ONLY public.product_specification DROP CONSTRAINT IF EXISTS product_spec_line_pkey;
ALTER TABLE IF EXISTS ONLY public.product_image DROP CONSTRAINT IF EXISTS product_image_pkey;
ALTER TABLE IF EXISTS ONLY public.product_component DROP CONSTRAINT IF EXISTS product_component_pkey;
ALTER TABLE IF EXISTS ONLY public.product_category DROP CONSTRAINT IF EXISTS product_category_pkey;
ALTER TABLE IF EXISTS ONLY public.image DROP CONSTRAINT IF EXISTS picture_pkey;
ALTER TABLE IF EXISTS ONLY public.payment DROP CONSTRAINT IF EXISTS payment_pkey;
ALTER TABLE IF EXISTS ONLY public.orders DROP CONSTRAINT IF EXISTS orders_pkey;
ALTER TABLE IF EXISTS ONLY public.order_status_code DROP CONSTRAINT IF EXISTS order_status_code_pkey;
ALTER TABLE IF EXISTS ONLY public.order_product DROP CONSTRAINT IF EXISTS order_product_pkey;
ALTER TABLE IF EXISTS ONLY public.product DROP CONSTRAINT IF EXISTS item_pkey;
ALTER TABLE IF EXISTS ONLY public.product_documentation DROP CONSTRAINT IF EXISTS documentation_product_pkey;
ALTER TABLE IF EXISTS ONLY public.shipment DROP CONSTRAINT IF EXISTS delivery_type_pkey;
ALTER TABLE IF EXISTS ONLY public.category_specification DROP CONSTRAINT IF EXISTS category_specification_pkey;
ALTER TABLE IF EXISTS ONLY public.category DROP CONSTRAINT IF EXISTS category_pkey;
ALTER TABLE IF EXISTS ONLY public.address DROP CONSTRAINT IF EXISTS address_pkey;
ALTER TABLE IF EXISTS public.warehouse_product ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.warehouse ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.users ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.specification ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.shipment ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.product_specification ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.product_image ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.product_documentation ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.product_component ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.product_category ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.product ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.payment ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.orders ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.order_status_code ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.order_product ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.image ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.category_specification ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.category ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.address ALTER COLUMN id DROP DEFAULT;
DROP TABLE IF EXISTS public.zip_code;
DROP SEQUENCE IF EXISTS public.warehouse_product_id_seq;
DROP TABLE IF EXISTS public.warehouse_product;
DROP SEQUENCE IF EXISTS public.warehouse_id_seq;
DROP TABLE IF EXISTS public.warehouse;
DROP SEQUENCE IF EXISTS public.users_id_seq;
DROP TABLE IF EXISTS public.users;
DROP SEQUENCE IF EXISTS public.specification_id_seq;
DROP TABLE IF EXISTS public.specification;
DROP SEQUENCE IF EXISTS public.product_spec_line_id_seq;
DROP TABLE IF EXISTS public.product_specification;
DROP SEQUENCE IF EXISTS public.product_image_id_seq;
DROP TABLE IF EXISTS public.product_image;
DROP SEQUENCE IF EXISTS public.product_component_id_seq;
DROP TABLE IF EXISTS public.product_component;
DROP SEQUENCE IF EXISTS public.product_category_id_seq;
DROP TABLE IF EXISTS public.product_category;
DROP SEQUENCE IF EXISTS public.picture_id_seq;
DROP SEQUENCE IF EXISTS public.payment_id_seq;
DROP TABLE IF EXISTS public.payment;
DROP SEQUENCE IF EXISTS public.orders_id_seq;
DROP TABLE IF EXISTS public.orders;
DROP SEQUENCE IF EXISTS public.order_status_code_id_seq;
DROP TABLE IF EXISTS public.order_status_code;
DROP SEQUENCE IF EXISTS public.order_product_id_seq;
DROP TABLE IF EXISTS public.order_product;
DROP SEQUENCE IF EXISTS public.item_id_seq;
DROP TABLE IF EXISTS public.product;
DROP TABLE IF EXISTS public.image;
DROP SEQUENCE IF EXISTS public.documentation_product_id_seq;
DROP TABLE IF EXISTS public.product_documentation;
DROP SEQUENCE IF EXISTS public.delivery_type_id_seq;
DROP TABLE IF EXISTS public.shipment;
DROP SEQUENCE IF EXISTS public.category_specification_id_seq;
DROP TABLE IF EXISTS public.category_specification;
DROP SEQUENCE IF EXISTS public.category_id_seq;
DROP TABLE IF EXISTS public.category;
DROP SEQUENCE IF EXISTS public.address_id_seq;
DROP TABLE IF EXISTS public.address;
DROP SCHEMA IF EXISTS public;

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- TOC entry 3527 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 223 (class 1259 OID 24607)
-- Name: address; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.address (
                                id bigint NOT NULL,
                                street character varying,
                                number bigint,
                                floor bigint,
                                extra_info character varying,
                                zip_code bigint NOT NULL
);


ALTER TABLE public.address OWNER TO postgres;

--
-- TOC entry 247 (class 1259 OID 24962)
-- Name: address_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.address_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.address_id_seq OWNER TO postgres;

--
-- TOC entry 3528 (class 0 OID 0)
-- Dependencies: 247
-- Name: address_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.address_id_seq OWNED BY public.address.id;


--
-- TOC entry 219 (class 1259 OID 24592)
-- Name: category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category (
                                 id bigint NOT NULL,
                                 name character varying NOT NULL
);


ALTER TABLE public.category OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 24795)
-- Name: category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.category_id_seq OWNER TO postgres;

--
-- TOC entry 3529 (class 0 OID 0)
-- Dependencies: 229
-- Name: category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.category_id_seq OWNED BY public.category.id;


--
-- TOC entry 253 (class 1259 OID 25020)
-- Name: category_specification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category_specification (
                                               id bigint NOT NULL,
                                               category_id bigint NOT NULL,
                                               specification_id bigint NOT NULL
);


ALTER TABLE public.category_specification OWNER TO postgres;

--
-- TOC entry 252 (class 1259 OID 25019)
-- Name: category_specification_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.category_specification_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.category_specification_id_seq OWNER TO postgres;

--
-- TOC entry 3530 (class 0 OID 0)
-- Dependencies: 252
-- Name: category_specification_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.category_specification_id_seq OWNED BY public.category_specification.id;


--
-- TOC entry 221 (class 1259 OID 24598)
-- Name: shipment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shipment (
                                 id bigint NOT NULL,
                                 name character varying,
                                 time_of_delivery timestamp without time zone,
                                 time_of_shipment timestamp without time zone
);


ALTER TABLE public.shipment OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 24863)
-- Name: delivery_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.delivery_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.delivery_type_id_seq OWNER TO postgres;

--
-- TOC entry 3531 (class 0 OID 0)
-- Dependencies: 231
-- Name: delivery_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.delivery_type_id_seq OWNED BY public.shipment.id;


--
-- TOC entry 220 (class 1259 OID 24595)
-- Name: product_documentation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_documentation (
                                              id bigint NOT NULL,
                                              name character varying,
                                              description character varying,
                                              data bytea,
                                              product_id bigint,
                                              type character varying,
                                              format character varying
);


ALTER TABLE public.product_documentation OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 24820)
-- Name: documentation_product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.documentation_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.documentation_product_id_seq OWNER TO postgres;

--
-- TOC entry 3532 (class 0 OID 0)
-- Dependencies: 230
-- Name: documentation_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.documentation_product_id_seq OWNED BY public.product_documentation.id;


--
-- TOC entry 228 (class 1259 OID 24644)
-- Name: image; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.image (
                              id bigint NOT NULL,
                              data bytea,
                              source character varying,
                              name character varying,
                              format character varying,
                              downscaled boolean
);


ALTER TABLE public.image OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 24580)
-- Name: product; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product (
                                id bigint NOT NULL,
                                name character varying,
                                description character varying,
                                links character varying[],
                                price numeric,
                                internal boolean DEFAULT false
);


ALTER TABLE public.product OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 24613)
-- Name: item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.item_id_seq OWNER TO postgres;

--
-- TOC entry 3533 (class 0 OID 0)
-- Dependencies: 225
-- Name: item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.item_id_seq OWNED BY public.product.id;


--
-- TOC entry 217 (class 1259 OID 24586)
-- Name: order_product; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_product (
                                      id bigint NOT NULL,
                                      order_id bigint NOT NULL,
                                      product_id bigint NOT NULL,
                                      quantity bigint NOT NULL
);


ALTER TABLE public.order_product OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 24937)
-- Name: order_product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.order_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.order_product_id_seq OWNER TO postgres;

--
-- TOC entry 3534 (class 0 OID 0)
-- Dependencies: 242
-- Name: order_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.order_product_id_seq OWNED BY public.order_product.id;


--
-- TOC entry 234 (class 1259 OID 24880)
-- Name: order_status_code; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_status_code (
                                          id bigint NOT NULL,
                                          name character varying,
                                          description character varying
);


ALTER TABLE public.order_status_code OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 24879)
-- Name: order_status_code_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.order_status_code_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.order_status_code_id_seq OWNER TO postgres;

--
-- TOC entry 3535 (class 0 OID 0)
-- Dependencies: 233
-- Name: order_status_code_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.order_status_code_id_seq OWNED BY public.order_status_code.id;


--
-- TOC entry 218 (class 1259 OID 24589)
-- Name: orders; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.orders (
                               id bigint NOT NULL,
                               customer_id bigint,
                               employee_id bigint,
                               status_code_id bigint,
                               time_of_order timestamp without time zone,
                               shipment_id bigint,
                               price numeric,
                               note character varying
);


ALTER TABLE public.orders OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 24872)
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.orders_id_seq OWNER TO postgres;

--
-- TOC entry 3536 (class 0 OID 0)
-- Dependencies: 232
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;


--
-- TOC entry 249 (class 1259 OID 24974)
-- Name: payment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.payment (
                                id bigint NOT NULL,
                                order_id bigint,
                                time_of_payment timestamp without time zone,
                                details character varying,
                                amount numeric
);


ALTER TABLE public.payment OWNER TO postgres;

--
-- TOC entry 248 (class 1259 OID 24973)
-- Name: payment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.payment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.payment_id_seq OWNER TO postgres;

--
-- TOC entry 3537 (class 0 OID 0)
-- Dependencies: 248
-- Name: payment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.payment_id_seq OWNED BY public.payment.id;


--
-- TOC entry 227 (class 1259 OID 24643)
-- Name: picture_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.picture_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.picture_id_seq OWNER TO postgres;

--
-- TOC entry 3538 (class 0 OID 0)
-- Dependencies: 227
-- Name: picture_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.picture_id_seq OWNED BY public.image.id;


--
-- TOC entry 246 (class 1259 OID 24956)
-- Name: product_category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_category (
                                         id bigint NOT NULL,
                                         product_id bigint,
                                         category_id bigint
);


ALTER TABLE public.product_category OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 24955)
-- Name: product_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_category_id_seq OWNER TO postgres;

--
-- TOC entry 3539 (class 0 OID 0)
-- Dependencies: 245
-- Name: product_category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.product_category_id_seq OWNED BY public.product_category.id;


--
-- TOC entry 216 (class 1259 OID 24583)
-- Name: product_component; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_component (
                                          id bigint NOT NULL,
                                          product_id bigint,
                                          component_id bigint,
                                          quantity bigint
);


ALTER TABLE public.product_component OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 24897)
-- Name: product_component_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_component_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_component_id_seq OWNER TO postgres;

--
-- TOC entry 3540 (class 0 OID 0)
-- Dependencies: 236
-- Name: product_component_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.product_component_id_seq OWNED BY public.product_component.id;


--
-- TOC entry 251 (class 1259 OID 25013)
-- Name: product_image; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_image (
                                      id bigint NOT NULL,
                                      product_id bigint,
                                      image_id bigint,
                                      image_downscaled_id bigint
);


ALTER TABLE public.product_image OWNER TO postgres;

--
-- TOC entry 250 (class 1259 OID 25012)
-- Name: product_image_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_image_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_image_id_seq OWNER TO postgres;

--
-- TOC entry 3541 (class 0 OID 0)
-- Dependencies: 250
-- Name: product_image_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.product_image_id_seq OWNED BY public.product_image.id;


--
-- TOC entry 226 (class 1259 OID 24640)
-- Name: product_specification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_specification (
                                              id bigint NOT NULL,
                                              product_id bigint,
                                              specification_id bigint,
                                              details character varying
);


ALTER TABLE public.product_specification OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 24888)
-- Name: product_spec_line_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_spec_line_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_spec_line_id_seq OWNER TO postgres;

--
-- TOC entry 3542 (class 0 OID 0)
-- Dependencies: 235
-- Name: product_spec_line_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.product_spec_line_id_seq OWNED BY public.product_specification.id;


--
-- TOC entry 244 (class 1259 OID 24947)
-- Name: specification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.specification (
                                      id bigint NOT NULL,
                                      name character varying,
                                      unit character varying
);


ALTER TABLE public.specification OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 24946)
-- Name: specification_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.specification_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.specification_id_seq OWNER TO postgres;

--
-- TOC entry 3543 (class 0 OID 0)
-- Dependencies: 243
-- Name: specification_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.specification_id_seq OWNED BY public.specification.id;


--
-- TOC entry 222 (class 1259 OID 24601)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
                              id bigint NOT NULL,
                              name character varying,
                              surname character varying,
                              address_id bigint,
                              email character varying,
                              role character varying,
                              password character varying
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 24927)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO postgres;

--
-- TOC entry 3544 (class 0 OID 0)
-- Dependencies: 241
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 238 (class 1259 OID 24905)
-- Name: warehouse; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.warehouse (
                                  id bigint NOT NULL,
                                  address_id bigint
);


ALTER TABLE public.warehouse OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 24904)
-- Name: warehouse_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.warehouse_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.warehouse_id_seq OWNER TO postgres;

--
-- TOC entry 3545 (class 0 OID 0)
-- Dependencies: 237
-- Name: warehouse_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.warehouse_id_seq OWNED BY public.warehouse.id;


--
-- TOC entry 240 (class 1259 OID 24912)
-- Name: warehouse_product; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.warehouse_product (
                                          id bigint NOT NULL,
                                          warehouse_id bigint,
                                          product_id bigint,
                                          quantity bigint
);


ALTER TABLE public.warehouse_product OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 24911)
-- Name: warehouse_product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.warehouse_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.warehouse_product_id_seq OWNER TO postgres;

--
-- TOC entry 3546 (class 0 OID 0)
-- Dependencies: 239
-- Name: warehouse_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.warehouse_product_id_seq OWNED BY public.warehouse_product.id;


--
-- TOC entry 224 (class 1259 OID 24610)
-- Name: zip_code; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.zip_code (
                                 name character varying NOT NULL,
                                 code bigint NOT NULL
);


ALTER TABLE public.zip_code OWNER TO postgres;

--
-- TOC entry 3306 (class 2604 OID 24963)
-- Name: address id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address ALTER COLUMN id SET DEFAULT nextval('public.address_id_seq'::regclass);


--
-- TOC entry 3302 (class 2604 OID 24796)
-- Name: category id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category ALTER COLUMN id SET DEFAULT nextval('public.category_id_seq'::regclass);


--
-- TOC entry 3316 (class 2604 OID 25023)
-- Name: category_specification id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_specification ALTER COLUMN id SET DEFAULT nextval('public.category_specification_id_seq'::regclass);


--
-- TOC entry 3308 (class 2604 OID 24647)
-- Name: image id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.image ALTER COLUMN id SET DEFAULT nextval('public.picture_id_seq'::regclass);


--
-- TOC entry 3300 (class 2604 OID 24938)
-- Name: order_product id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_product ALTER COLUMN id SET DEFAULT nextval('public.order_product_id_seq'::regclass);


--
-- TOC entry 3309 (class 2604 OID 24883)
-- Name: order_status_code id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_status_code ALTER COLUMN id SET DEFAULT nextval('public.order_status_code_id_seq'::regclass);


--
-- TOC entry 3301 (class 2604 OID 24873)
-- Name: orders id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);


--
-- TOC entry 3314 (class 2604 OID 24977)
-- Name: payment id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment ALTER COLUMN id SET DEFAULT nextval('public.payment_id_seq'::regclass);


--
-- TOC entry 3297 (class 2604 OID 24614)
-- Name: product id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product ALTER COLUMN id SET DEFAULT nextval('public.item_id_seq'::regclass);


--
-- TOC entry 3313 (class 2604 OID 24959)
-- Name: product_category id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_category ALTER COLUMN id SET DEFAULT nextval('public.product_category_id_seq'::regclass);


--
-- TOC entry 3299 (class 2604 OID 24898)
-- Name: product_component id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_component ALTER COLUMN id SET DEFAULT nextval('public.product_component_id_seq'::regclass);


--
-- TOC entry 3303 (class 2604 OID 24821)
-- Name: product_documentation id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_documentation ALTER COLUMN id SET DEFAULT nextval('public.documentation_product_id_seq'::regclass);


--
-- TOC entry 3315 (class 2604 OID 25016)
-- Name: product_image id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_image ALTER COLUMN id SET DEFAULT nextval('public.product_image_id_seq'::regclass);


--
-- TOC entry 3307 (class 2604 OID 24889)
-- Name: product_specification id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_specification ALTER COLUMN id SET DEFAULT nextval('public.product_spec_line_id_seq'::regclass);


--
-- TOC entry 3304 (class 2604 OID 24864)
-- Name: shipment id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment ALTER COLUMN id SET DEFAULT nextval('public.delivery_type_id_seq'::regclass);


--
-- TOC entry 3312 (class 2604 OID 24950)
-- Name: specification id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.specification ALTER COLUMN id SET DEFAULT nextval('public.specification_id_seq'::regclass);


--
-- TOC entry 3305 (class 2604 OID 24928)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 3310 (class 2604 OID 24908)
-- Name: warehouse id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.warehouse ALTER COLUMN id SET DEFAULT nextval('public.warehouse_id_seq'::regclass);


--
-- TOC entry 3311 (class 2604 OID 24915)
-- Name: warehouse_product id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.warehouse_product ALTER COLUMN id SET DEFAULT nextval('public.warehouse_product_id_seq'::regclass);


--
-- TOC entry 3334 (class 2606 OID 24970)
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- TOC entry 3326 (class 2606 OID 24803)
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- TOC entry 3356 (class 2606 OID 25025)
-- Name: category_specification category_specification_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_specification
    ADD CONSTRAINT category_specification_pkey PRIMARY KEY (id);


--
-- TOC entry 3330 (class 2606 OID 24871)
-- Name: shipment delivery_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment
    ADD CONSTRAINT delivery_type_pkey PRIMARY KEY (id);


--
-- TOC entry 3328 (class 2606 OID 24828)
-- Name: product_documentation documentation_product_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_documentation
    ADD CONSTRAINT documentation_product_pkey PRIMARY KEY (id);


--
-- TOC entry 3318 (class 2606 OID 24621)
-- Name: product item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT item_pkey PRIMARY KEY (id);


--
-- TOC entry 3322 (class 2606 OID 24943)
-- Name: order_product order_product_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_product
    ADD CONSTRAINT order_product_pkey PRIMARY KEY (id);


--
-- TOC entry 3342 (class 2606 OID 24887)
-- Name: order_status_code order_status_code_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_status_code
    ADD CONSTRAINT order_status_code_pkey PRIMARY KEY (id);


--
-- TOC entry 3324 (class 2606 OID 24878)
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- TOC entry 3352 (class 2606 OID 24981)
-- Name: payment payment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (id);


--
-- TOC entry 3340 (class 2606 OID 24651)
-- Name: image picture_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.image
    ADD CONSTRAINT picture_pkey PRIMARY KEY (id);


--
-- TOC entry 3350 (class 2606 OID 24961)
-- Name: product_category product_category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_category
    ADD CONSTRAINT product_category_pkey PRIMARY KEY (id);


--
-- TOC entry 3320 (class 2606 OID 24903)
-- Name: product_component product_component_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_component
    ADD CONSTRAINT product_component_pkey PRIMARY KEY (id);


--
-- TOC entry 3354 (class 2606 OID 25018)
-- Name: product_image product_image_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_image
    ADD CONSTRAINT product_image_pkey PRIMARY KEY (id);


--
-- TOC entry 3338 (class 2606 OID 24896)
-- Name: product_specification product_spec_line_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_specification
    ADD CONSTRAINT product_spec_line_pkey PRIMARY KEY (id);


--
-- TOC entry 3348 (class 2606 OID 24954)
-- Name: specification specification_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.specification
    ADD CONSTRAINT specification_pkey PRIMARY KEY (id);


--
-- TOC entry 3332 (class 2606 OID 24935)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3344 (class 2606 OID 24910)
-- Name: warehouse warehouse_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.warehouse
    ADD CONSTRAINT warehouse_pkey PRIMARY KEY (id);


--
-- TOC entry 3346 (class 2606 OID 24917)
-- Name: warehouse_product warehouse_product_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.warehouse_product
    ADD CONSTRAINT warehouse_product_pkey PRIMARY KEY (id);


--
-- TOC entry 3336 (class 2606 OID 24972)
-- Name: zip_code zip_code_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zip_code
    ADD CONSTRAINT zip_code_pkey PRIMARY KEY (code);


--
-- TOC entry 3366 (class 2606 OID 25158)
-- Name: users fk address id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT "fk address id" FOREIGN KEY (address_id) REFERENCES public.address(id) NOT VALID;


--
-- TOC entry 3372 (class 2606 OID 25098)
-- Name: product_category fk category id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_category
    ADD CONSTRAINT "fk category id" FOREIGN KEY (category_id) REFERENCES public.category(id) NOT VALID;


--
-- TOC entry 3377 (class 2606 OID 25053)
-- Name: category_specification fk catid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_specification
    ADD CONSTRAINT "fk catid" FOREIGN KEY (category_id) REFERENCES public.category(id) NOT VALID;


--
-- TOC entry 3357 (class 2606 OID 25108)
-- Name: product_component fk component id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_component
    ADD CONSTRAINT "fk component id" FOREIGN KEY (component_id) REFERENCES public.product(id) NOT VALID;


--
-- TOC entry 3361 (class 2606 OID 25073)
-- Name: orders fk customer id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT "fk customer id" FOREIGN KEY (customer_id) REFERENCES public.users(id) NOT VALID;


--
-- TOC entry 3362 (class 2606 OID 25078)
-- Name: orders fk employee id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT "fk employee id" FOREIGN KEY (employee_id) REFERENCES public.users(id) NOT VALID;


--
-- TOC entry 3374 (class 2606 OID 25128)
-- Name: product_image fk img downscaled id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_image
    ADD CONSTRAINT "fk img downscaled id" FOREIGN KEY (image_downscaled_id) REFERENCES public.image(id) NOT VALID;


--
-- TOC entry 3375 (class 2606 OID 25123)
-- Name: product_image fk img id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_image
    ADD CONSTRAINT "fk img id" FOREIGN KEY (image_id) REFERENCES public.image(id) NOT VALID;


--
-- TOC entry 3359 (class 2606 OID 25063)
-- Name: order_product fk orderid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_product
    ADD CONSTRAINT "fk orderid" FOREIGN KEY (order_id) REFERENCES public.orders(id) NOT VALID;


--
-- TOC entry 3373 (class 2606 OID 25093)
-- Name: product_category fk product id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_category
    ADD CONSTRAINT "fk product id" FOREIGN KEY (product_id) REFERENCES public.product(id) NOT VALID;


--
-- TOC entry 3358 (class 2606 OID 25103)
-- Name: product_component fk product id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_component
    ADD CONSTRAINT "fk product id" FOREIGN KEY (product_id) REFERENCES public.product(id) NOT VALID;


--
-- TOC entry 3365 (class 2606 OID 25113)
-- Name: product_documentation fk product id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_documentation
    ADD CONSTRAINT "fk product id" FOREIGN KEY (product_id) REFERENCES public.product(id) NOT VALID;


--
-- TOC entry 3376 (class 2606 OID 25118)
-- Name: product_image fk product id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_image
    ADD CONSTRAINT "fk product id" FOREIGN KEY (product_id) REFERENCES public.product(id) NOT VALID;


--
-- TOC entry 3368 (class 2606 OID 25133)
-- Name: product_specification fk product id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_specification
    ADD CONSTRAINT "fk product id" FOREIGN KEY (product_id) REFERENCES public.product(id) NOT VALID;


--
-- TOC entry 3370 (class 2606 OID 25148)
-- Name: warehouse_product fk product id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.warehouse_product
    ADD CONSTRAINT "fk product id" FOREIGN KEY (product_id) REFERENCES public.product(id) NOT VALID;


--
-- TOC entry 3360 (class 2606 OID 25068)
-- Name: order_product fk productid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_product
    ADD CONSTRAINT "fk productid" FOREIGN KEY (product_id) REFERENCES public.product(id) NOT VALID;


--
-- TOC entry 3363 (class 2606 OID 25088)
-- Name: orders fk shipment id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT "fk shipment id" FOREIGN KEY (shipment_id) REFERENCES public.shipment(id) NOT VALID;


--
-- TOC entry 3378 (class 2606 OID 25058)
-- Name: category_specification fk specid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_specification
    ADD CONSTRAINT "fk specid" FOREIGN KEY (specification_id) REFERENCES public.specification(id) NOT VALID;


--
-- TOC entry 3369 (class 2606 OID 25138)
-- Name: product_specification fk specification id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_specification
    ADD CONSTRAINT "fk specification id" FOREIGN KEY (specification_id) REFERENCES public.specification(id) NOT VALID;


--
-- TOC entry 3364 (class 2606 OID 25083)
-- Name: orders fk status code; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT "fk status code" FOREIGN KEY (status_code_id) REFERENCES public.order_status_code(id) NOT VALID;


--
-- TOC entry 3371 (class 2606 OID 25143)
-- Name: warehouse_product fk warehouse id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.warehouse_product
    ADD CONSTRAINT "fk warehouse id" FOREIGN KEY (warehouse_id) REFERENCES public.warehouse(id) NOT VALID;


--
-- TOC entry 3367 (class 2606 OID 25153)
-- Name: address fk zip code; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT "fk zip code" FOREIGN KEY (zip_code) REFERENCES public.zip_code(code) NOT VALID;


-- Completed on 2024-05-17 13:15:59 UTC

--
-- PostgreSQL database dump complete
--