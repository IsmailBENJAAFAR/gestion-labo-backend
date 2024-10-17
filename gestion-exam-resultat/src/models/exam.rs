use sqlx::postgres::PgRow;
use sqlx::Row;

#[derive(Debug)]
pub struct Exam {
    id: i32,
    nom: String,
    created_at: chrono::DateTime<chrono::Utc>,
    fk_id_analyse: i32,
}

impl TryFrom<PgRow> for Exam {
    type Error = anyhow::Error;
    fn try_from(row: PgRow) -> std::result::Result<Self, Self::Error> {
        Ok(Exam {
            id: row.try_get("id")?,
            nom: row.try_get("nom")?,
            created_at: row.try_get("created_at")?,
            fk_id_analyse: row.try_get("fk_id_analyse")?,
        })
    }
}
