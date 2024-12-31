-- Add up migration script here
CREATE TABLE epreuve (
	id SERIAL PRIMARY KEY,
	nom VARCHAR(255),
	fk_id_analyse INTEGER,
	created_at TIMESTAMPTZ NOT NULL,
	updated_at TIMESTAMPTZ
);
