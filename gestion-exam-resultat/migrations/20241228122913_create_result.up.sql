-- Add up migration script here
CREATE TABLE resultat (
	id SERIAL PRIMARY KEY,
	exam_id INTEGER REFERENCES exam(id),
	observation TEXT,
	score INTEGER,
	created_at TIMESTAMPTZ NOT NULL,
	updated_at TIMESTAMPTZ
)
