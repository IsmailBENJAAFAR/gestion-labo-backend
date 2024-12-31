use serde::{Deserialize, Serialize};
use sqlx::postgres::PgRow;
use sqlx::Row;

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq, Eq)]
pub struct Epreuve {
    pub id: i32,
    pub nom: String,
    #[serde(rename = "analyseId")]
    pub fk_id_analyse: i32,
    #[serde(rename = "createdAt")]
    pub created_at: chrono::DateTime<chrono::Utc>,
    #[serde(rename = "updatedAt")]
    pub updated_at: Option<chrono::DateTime<chrono::Utc>>,
}

impl Epreuve {
    pub fn new(nom: &str, fk_id_analyse: i32) -> Epreuve {
        Epreuve {
            id: 0,
            nom: nom.to_string(),
            fk_id_analyse,
            created_at: chrono::Utc::now(),
            updated_at: None,
        }
    }

    pub fn with_id(id: i32, nom: &str, fk_id_analyse: i32) -> Epreuve {
        Epreuve {
            id,
            nom: nom.to_string(),
            fk_id_analyse,
            created_at: chrono::Utc::now(),
            updated_at: None,
        }
    }
}

impl TryFrom<PgRow> for Epreuve {
    type Error = anyhow::Error;
    fn try_from(row: PgRow) -> std::result::Result<Self, Self::Error> {
        Ok(Epreuve {
            id: row.try_get("id")?,
            nom: row.try_get("nom")?,
            fk_id_analyse: row.try_get("fk_id_analyse")?,
            created_at: row.try_get("created_at")?,
            updated_at: row.try_get("updated_at")?,
        })
    }
}
