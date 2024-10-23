use serde::{Deserialize, Serialize};
use sqlx::postgres::PgRow;
use sqlx::Row;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Exam {
    pub id: i32,
    pub fk_num_dossier: i32,
    pub fk_id_epreuve: i32,
    pub fk_id_test_analyse: i32,
    pub created_at: chrono::DateTime<chrono::Utc>,
    pub updated_at: Option<chrono::DateTime<chrono::Utc>>,
}

impl Exam {
    pub fn new(fk_num_dossier: i32, fk_id_epreuve: i32, fk_id_test_analyse: i32) -> Exam {
        Exam {
            id: 0,
            fk_num_dossier,
            fk_id_epreuve,
            fk_id_test_analyse,
            created_at: chrono::Utc::now(),
            updated_at: None,
        }
    }
}

impl TryFrom<PgRow> for Exam {
    type Error = anyhow::Error;
    fn try_from(row: PgRow) -> std::result::Result<Self, Self::Error> {
        Ok(Exam {
            id: row.try_get("id")?,
            fk_num_dossier: row.try_get("fk_num_dossier")?,
            fk_id_epreuve: row.try_get("fk_id_epreuve")?,
            fk_id_test_analyse: row.try_get("fk_id_test_analyse")?,
            created_at: row.try_get("created_at")?,
            updated_at: row.try_get("updated_at")?,
        })
    }
}
