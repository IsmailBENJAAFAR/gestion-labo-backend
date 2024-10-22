use anyhow::Result;
use sqlx::{Pool, Postgres};

use super::dao_interface::Dao;
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
            sqlx::query("INSERT INTO exam (fk_num_dossier, fk_id_epreuve, fk_id_test_analyse) VALUES ($1, $2, $3)")
                .bind(data.fk_num_dossier)
                .bind(data.fk_id_epreuve)
                .bind(data.fk_id_test_analyse)
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
