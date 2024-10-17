use std::sync::Arc;

use anyhow::Result;
use sqlx::{postgres::PgPoolOptions, Pool, Postgres};
use tokio::sync::OnceCell;

use super::dao::Dao;
use crate::models::Exam;

static CONNECTION: OnceCell<Arc<Pool<Postgres>>> = OnceCell::const_new();

async fn get_connection() -> Arc<Pool<Postgres>> {
    CONNECTION
        .get_or_init(|| async {
            let url = std::env::var("DATABASE_URL").unwrap();
            Arc::new(
                PgPoolOptions::new()
                    .max_connections(5)
                    .connect(&url)
                    .await
                    .unwrap(),
            )
        })
        .await
        .clone()
}

pub struct ExamDao {}

impl ExamDao {
    pub fn new() -> ExamDao {
        ExamDao {}
    }
}

impl Dao<Exam> for ExamDao {
    async fn find(&self, id: i32) -> Result<Exam> {
        todo!()
    }

    async fn insert(&self, data: Exam) -> Result<bool> {
        let pool = get_connection().await;
        // let conn = pool.lock().await;
        let res =
            sqlx::query("INSERT INTO exam (nom, created_at, fk_id_analyse) VALUES ($1, $2, $3)")
                .bind(data.nom.clone())
                .bind(data.created_at)
                .bind(data.fk_id_analyse)
                .execute(pool.as_ref())
                .await?;

        Ok(res.rows_affected() == 1)
    }

    async fn remove(&self, id: i32) -> bool {
        false
    }

    async fn find_all(&self) -> Result<Vec<Exam>> {
        // let conn = pool.lock().await;
        let res = sqlx::query("SELECT * FROM exam")
            .fetch_all(get_connection().await.as_ref())
            .await?;
        let mut exams: Vec<Exam> = Vec::new();
        for entry in res {
            exams.push(Exam::try_from(entry)?);
        }

        Ok(exams)
    }
}
