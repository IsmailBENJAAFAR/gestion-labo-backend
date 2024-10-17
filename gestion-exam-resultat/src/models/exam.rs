use serde::{Deserialize, Serialize};
use sqlx::postgres::PgRow;
use sqlx::Row;

#[derive(Debug, Serialize, Deserialize)]
pub struct Exam {
    pub id: i32,
    pub nom: String,
    pub created_at: chrono::DateTime<chrono::Utc>,
    pub fk_id_analyse: i32,
}

impl Exam {
    pub fn new(nom: String, fk_id_analyse: i32) -> Exam {
        Exam {
            id: 0,
            nom,
            created_at: chrono::Utc::now(),
            fk_id_analyse,
        }
    }
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
