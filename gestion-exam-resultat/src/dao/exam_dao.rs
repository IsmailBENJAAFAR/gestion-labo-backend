use std::sync::Arc;

use sqlx::{postgres::PgPoolOptions, PgConnection, Pool, Postgres};
use tokio::sync::{Mutex, OnceCell};

use super::dao::Dao;
use crate::models::Exam;

static CONNECTION: OnceCell<Arc<Mutex<Pool<Postgres>>>> = OnceCell::const_new();

async fn get_connection() -> Arc<Mutex<Pool<Postgres>>> {
    CONNECTION
        .get_or_init(|| async {
            let url = std::env::var("DATABASE_URL").unwrap();
            Arc::new(Mutex::new(
                PgPoolOptions::new()
                    .max_connections(5)
                    .connect(&url)
                    .await
                    .unwrap(),
            ))
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

impl<Exam> Dao<Exam> for ExamDao {
    fn find(&self, id: i32) -> anyhow::Result<Exam> {
        todo!()
    }

    fn insert(&self, data: Exam) -> bool {
        false
    }

    fn remove(&self, id: i32) -> bool {
        false
    }

    fn find_all(&self) -> anyhow::Result<Vec<Exam>> {
        Ok(Vec::new())
    }
}
