--
-- PostgreSQL database dump
--

-- Dumped from database version 12.5
-- Dumped by pg_dump version 12.5

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
-- Name: POCMultiUserChat; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE "MultiUserChat" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'French_France.1252' LC_CTYPE = 'French_France.1252';


ALTER DATABASE "MultiUserChat" OWNER TO postgres;

\connect "POCMultiUserChat"

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: chatuser; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chatuser (
    login text NOT NULL,
    password text NOT NULL
);


ALTER TABLE public.chatuser OWNER TO postgres;

--
-- Name: privatemessage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.privatemessage (
    sender text NOT NULL,
    recipient text NOT NULL,
    body text NOT NULL
);


ALTER TABLE public.privatemessage OWNER TO postgres;

--
-- Name: topicmessage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.topicmessage (
    topic text NOT NULL,
    body text NOT NULL,
    sender text NOT NULL
);


ALTER TABLE public.topicmessage OWNER TO postgres;

--
-- Name: topicsfollowed; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.topicsfollowed (
    login text NOT NULL,
    topics text[]
);


ALTER TABLE public.topicsfollowed OWNER TO postgres;

--
-- Data for Name: chatuser; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.chatuser VALUES ('Thomas', 'Thomas');
INSERT INTO public.chatuser VALUES ('Yann', 'Yann');
INSERT INTO public.chatuser VALUES ('Lucas', 'Lucas');
INSERT INTO public.chatuser VALUES ('Nadine', 'Nadine');


--
-- Data for Name: privatemessage; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: topicmessage; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: topicsfollowed; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: chatuser User_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chatuser
    ADD CONSTRAINT "User_pkey" PRIMARY KEY (login);


--
-- Name: topicsfollowed topicsfollowed_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.topicsfollowed
    ADD CONSTRAINT topicsfollowed_pkey PRIMARY KEY (login);


--
-- PostgreSQL database dump complete
--

