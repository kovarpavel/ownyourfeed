CREATE SCHEMA rss_database;

CREATE TABLE rss_database.source (
	id int4 GENERATED ALWAYS AS identity NOT NULL,
	title varchar(512) NULL,
	url varchar(512) NOT NULL,
	description varchar(2048) NULL,
	CONSTRAINT sources_pkey PRIMARY KEY (id),
	CONSTRAINT sources_un UNIQUE (url)
);

CREATE TABLE rss_database.user (
	id int4 GENERATED ALWAYS AS identity NOT NULL,
	username varchar NOT NULL,
	email varchar NULL,
	"password" varchar NULL,
	CONSTRAINT users_pk PRIMARY KEY (id),
	CONSTRAINT users_un UNIQUE (username)
);

CREATE TABLE rss_database.user_source (
	user_id int4 NOT NULL,
	source_id   int4 NOT NULL,
	CONSTRAINT user_source_pk PRIMARY KEY (user_id, source_id)
);

CREATE TABLE rss_database.item (
	id int4 GENERATED ALWAYS AS identity NOT NULL,
	title varchar NULL,
	link varchar NULL,
	description varchar NULL,
	guid varchar NULL,
	author varchar NULL,
	date timestamp NULL,
	source_id int4 NOT NULL,
	CONSTRAINT items_pkey PRIMARY KEY (id),
	CONSTRAINT items_un UNIQUE (guid)
);