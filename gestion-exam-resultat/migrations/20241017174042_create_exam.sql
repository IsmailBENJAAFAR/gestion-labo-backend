-- Add migration script here
CREATE TABLE exam (
	id SERIAL PRIMARY KEY,
	nom VARCHAR (64) NOT NULL,
	created_at TIMESTAMPTZ NOT NULL
);
