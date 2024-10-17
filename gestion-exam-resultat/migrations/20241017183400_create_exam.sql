-- Add migration script here
DROP TABLE exam;
CREATE TABLE exam (
	id SERIAL PRIMARY KEY,
	nom VARCHAR (64) NOT NULL,
	created_at TIMESTAMPTZ NOT NULL,
	fk_id_analyse INTEGER
);
