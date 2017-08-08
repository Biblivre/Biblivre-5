CREATE ROLE biblivre LOGIN
  ENCRYPTED PASSWORD 'md52690de6f151b12923e0527bb496da66f'
  SUPERUSER INHERIT CREATEDB CREATEROLE;

CREATE DATABASE biblivre4
  WITH OWNER = biblivre
       ENCODING = 'UTF8'
       CONNECTION LIMIT = -1;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;
