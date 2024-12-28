use crate::{dao::interface::Dao, exam::model::Exam};
use anyhow::Result;
use axum::async_trait;
use sqlx::Row;
use sqlx::{Pool, Postgres};

pub mod resultat_dao;

#[derive(Clone)]
pub struct ExamDao {
    pool: Pool<Postgres>,
}

impl ExamDao {
    pub fn new(pool: Pool<Postgres>) -> ExamDao {
        ExamDao { pool }
    }
}

// TODO: populate the vec of Resultat for a specific exam id
// NOTE: In order to do the abov TODO, you have to finish dao for resultat alone be3da.
// So that you can load results with a specific exam id
// NOTE: Can be done with two types of loading, eager/lazy. In which case depending on how you want
// to load it, you can have eager load with the vec of results while lazy keeps the vec empty.

#[async_trait]
impl Dao<Exam> for ExamDao {
    async fn find(&self, id: i32) -> Result<Exam> {
        let res = sqlx::query("SELECT * from exam WHERE id = $1")
            .bind(id)
            .fetch_one(&self.pool)
            .await?;
        let exam = Exam::try_from(res)?;

        Ok(exam)
    }

    async fn insert(&self, data: &Exam) -> Result<i32> {
        let res =
            sqlx::query("INSERT INTO exam (fk_num_dossier, fk_id_epreuve, fk_id_test_analyse, created_at) VALUES ($1, $2, $3, $4) RETURNING id")
                .bind(data.fk_num_dossier)
                .bind(data.fk_id_epreuve)
                .bind(data.fk_id_test_analyse)
                .bind(data.created_at)
                .fetch_one(&self.pool)
                .await?;
        let id: i32 = res.try_get("id")?;
        Ok(id)
    }

    async fn update(&self, data: &Exam) -> Result<Exam> {
        let updated_at = chrono::Utc::now();
        let res = sqlx::query("UPDATE exam SET fk_num_dossier = $1, fk_id_epreuve = $2, fk_id_test_analyse = $3, updated_at = $4 WHERE id = $5 RETURNING *")
            .bind(data.fk_num_dossier)
            .bind(data.fk_id_epreuve)
            .bind(data.fk_id_test_analyse)
            .bind(updated_at)
            .bind(data.id)
            .fetch_one(&self.pool)
            .await?;

        let exam = Exam::try_from(res)?;

        Ok(exam)
    }

    async fn remove(&self, id: i32) -> Result<bool> {
        let res = sqlx::query("DELETE FROM exam WHERE id = $1")
            .bind(id)
            .execute(&self.pool)
            .await?;

        Ok(res.rows_affected() == 1)
    }

    async fn find_all(&self) -> Result<Vec<Exam>> {
        let res = sqlx::query("SELECT * FROM exam")
            .fetch_all(&self.pool)
            .await?;
        let mut exams: Vec<Exam> = Vec::new();
        for entry in res {
            exams.push(Exam::try_from(entry)?);
        }

        Ok(exams)
    }
}
