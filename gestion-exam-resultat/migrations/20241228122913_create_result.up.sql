-- Add up migration script here
CREATE TABLE resultat (
	id SERIAL PRIMARY KEY,
	exam_id INTEGER REFERENCES exam(id),
	observation TEXT,
	blood_pressure_systolic INTEGER, -- (ex: 120/80 mmHg).
	blood_pressure_diastolic INTEGER,
	glucose_level INTEGER, -- (ex: 95 mg/dL or 5 mmol/L).
	cholesterol_level INTEGER, -- (ex: 200 mg/dL)
	weight FLOAT, -- (ex: 55.5kg or 75.3kg)
	test_outcome VARCHAR(12), -- (ex: "positive" | "negative") (makes it a classification problem)
	recommendations TEXT,
	created_at TIMESTAMPTZ NOT NULL,
	updated_at TIMESTAMPTZ
)
