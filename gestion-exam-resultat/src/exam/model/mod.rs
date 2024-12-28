use serde::{Deserialize, Serialize};
use sqlx::postgres::PgRow;
use sqlx::Row;

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq, Eq)]
pub struct Exam {
    pub id: i32,
    #[serde(rename = "dossierId")]
    pub fk_num_dossier: i32,
    #[serde(rename = "epreuveId")]
    pub fk_id_epreuve: i32,
    #[serde(rename = "testAnalyseId")]
    pub fk_id_test_analyse: i32,
    #[serde(rename = "createdAt")]
    pub created_at: chrono::DateTime<chrono::Utc>,
    #[serde(rename = "updatedAt")]
    pub updated_at: Option<chrono::DateTime<chrono::Utc>>,
}

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq, Eq)]
pub struct Resultat {
    pub id: i32,
    #[serde(rename = "examId")]
    pub fk_id_exam: i32,
    pub observation: String,
    pub score: i32,
    #[serde(rename = "createdAt")]
    pub created_at: chrono::DateTime<chrono::Utc>,
    #[serde(rename = "updatedAt")]
    pub updated_at: Option<chrono::DateTime<chrono::Utc>>,
}

impl Resultat {
    pub fn new(fk_id_exam: i32, observation: &str, score: i32) -> Resultat {
        Resultat {
            id: 0,
            fk_id_exam,
            observation: observation.to_string(),
            score,
            created_at: chrono::Utc::now(),
            updated_at: None,
        }
    }

    pub fn with_id(id: i32, fk_id_exam: i32, observation: &str, score: i32) -> Resultat {
        Resultat {
            id,
            fk_id_exam,
            observation: observation.to_string(),
            score,
            created_at: chrono::Utc::now(),
            updated_at: None,
        }
    }
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

    pub fn with_id(
        id: i32,
        fk_num_dossier: i32,
        fk_id_epreuve: i32,
        fk_id_test_analyse: i32,
    ) -> Exam {
        Exam {
            id,
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

impl TryFrom<PgRow> for Resultat {
    type Error = anyhow::Error;
    fn try_from(row: PgRow) -> std::result::Result<Self, Self::Error> {
        Ok(Resultat {
            id: row.try_get("id")?,
            fk_id_exam: row.try_get("fk_id_exam")?,
            observation: row.try_get("observation")?,
            score: row.try_get("score")?,
            created_at: row.try_get("created_at")?,
            updated_at: row.try_get("updated_at")?,
        })
    }
}
