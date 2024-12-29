use anyhow::Result;
use axum::async_trait;
use sqlx::{Pool, Postgres};

use crate::dao::interface::Dao;

use super::model::Resultat;

#[derive(Clone)]
pub struct ResultatDao {
    pool: Pool<Postgres>,
}

impl ResultatDao {
    pub fn new(pool: Pool<Postgres>) -> ResultatDao {
        ResultatDao { pool }
    }
}

#[async_trait]
impl Dao<Resultat> for ResultatDao {
    async fn insert(&self, data: &Resultat) -> Result<i32> {
        todo!("implement insert resultat")
    }
    async fn update(&self, data: &Resultat) -> Result<Resultat> {
        todo!("implement update resultat")
    }
    async fn remove(&self, id: i32) -> Result<bool> {
        todo!("implement remove resultat")
    }
    async fn find(&self, id: i32) -> Result<Resultat> {
        todo!("implement find resultat")
    }
    async fn find_all(&self) -> Result<Vec<Resultat>> {
        todo!("implement find_all resultats")
    }
}
