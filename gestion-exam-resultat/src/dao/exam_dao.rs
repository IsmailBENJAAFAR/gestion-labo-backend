use anyhow::Result;
use sqlx::{Pool, Postgres};

use super::dao::Dao;
use crate::models::Exam;

#[derive(Clone)]
pub struct ExamDao {
    pool: Pool<Postgres>,
}

impl ExamDao {
    pub fn new(pool: Pool<Postgres>) -> ExamDao {
        ExamDao { pool }
    }
}

impl Dao<Exam> for ExamDao {
    async fn find(&self, id: i32) -> Result<Exam> {
        let res = sqlx::query("SELECT * from exam WHERE id = $1")
            .bind(id)
            .fetch_one(&self.pool)
            .await?;
        let exam = Exam::try_from(res)?;

        Ok(exam)
    }

    async fn insert(&self, data: Exam) -> Result<bool> {
        let res =
            sqlx::query("INSERT INTO exam (nom, created_at, fk_id_analyse) VALUES ($1, $2, $3)")
                .bind(data.nom.clone())
                .bind(data.created_at)
                .bind(data.fk_id_analyse)
                .execute(&self.pool)
                .await?;

        Ok(res.rows_affected() == 1)
    }

    async fn remove(&self, id: i32) -> Result<bool> {
        let res = sqlx::query("DELETE FROM exam WHERE id = $1")
            .bind(id)
            .execute(&self.pool)
            .await?;

        Ok(res.rows_affected() == 1)
    }

    async fn find_all(&self) -> Result<Vec<Exam>> {
        // let conn = pool.lock().await;
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
