-- Add up migration script here
CREATE TABLE exam (
	id SERIAL PRIMARY KEY,
	fk_num_dossier INTEGER,
	fk_id_epreuve INTEGER,
	fk_id_test_analyse INTEGER,
	created_at TIMESTAMPTZ NOT NULL,
	updated_at TIMESTAMPTZ,
);
