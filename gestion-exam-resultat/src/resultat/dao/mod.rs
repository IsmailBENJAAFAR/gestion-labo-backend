use anyhow::Result;
use axum::async_trait;
use sqlx::Row;
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
    async fn find(&self, id: i32) -> Result<Resultat> {
        let res = sqlx::query("SELECT * from resultat WHERE id = $1")
            .bind(id)
            .fetch_one(&self.pool)
            .await?;

        let resultat = Resultat::try_from(res)?;

        Ok(resultat)
    }

    async fn insert(&self, data: &Resultat) -> Result<i32> {
        let res = sqlx::query("INSERT INTO resultat (fk_id_exam, observation, score, created_at) VALUES ($1, $2, $3, $4) RETURNING id")
            .bind(data.fk_id_exam)
            .bind(&data.observation)
            .bind(data.score)
            .bind(data.created_at)
            .fetch_one(&self.pool)
            .await?;
        let id: i32 = res.try_get("id")?;
        Ok(id)
    }

    async fn update(&self, data: &Resultat) -> Result<Resultat> {
        let updated_at = chrono::Utc::now();
        let res = sqlx::query("UPDATE resultat SET observation = $1, score = $2, updated_at = $3 WHERE id = $4 RETURNING *")
            .bind(&data.observation)
            .bind(data.score)
            .bind(updated_at)
            .bind(data.id)
            .fetch_one(&self.pool)
            .await?;

        let resultat = Resultat::try_from(res)?;
        Ok(resultat)
    }

    async fn remove(&self, id: i32) -> Result<bool> {
        let res = sqlx::query("DELETE FROM resultat WHERE id = $1")
            .bind(id)
            .execute(&self.pool)
            .await?;

        Ok(res.rows_affected() == 1)
    }

    async fn find_all(&self) -> Result<Vec<Resultat>> {
        let res = sqlx::query("SELECT * FROM resultat")
            .fetch_all(&self.pool)
            .await?;
        let mut resultats: Vec<Resultat> = Vec::new();
        for entry in res {
            resultats.push(Resultat::try_from(entry)?);
        }

        Ok(resultats)
    }
}
