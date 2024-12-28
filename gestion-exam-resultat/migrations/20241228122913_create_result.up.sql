-- Add up migration script here
CREATE TABLE resultat (
	id SERIAL PRIMARY KEY,
	fk_id_exam INTEGER,
	observation TEXT,
	score INTEGER,
	created_at TIMESTAMPTZ NOT NULL,
	updated_at TIMESTAMPTZ
)
