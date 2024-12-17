use anyhow::Result;
use axum::async_trait;
use sqlx::Row;
use sqlx::{Pool, Postgres};

use crate::{dao::interface::Dao, epreuve::model::Epreuve};

#[derive(Clone)]
pub struct EpreuveDao {
    pool: Pool<Postgres>,
}

impl EpreuveDao {
    pub fn new(pool: Pool<Postgres>) -> EpreuveDao {
        EpreuveDao { pool }
    }
}

#[async_trait]
impl Dao<Epreuve> for EpreuveDao {
    async fn find(&self, id: i32) -> Result<Epreuve> {
        let res = sqlx::query("SELECT * from epreuve WHERE id = $1")
            .bind(id)
            .fetch_one(&self.pool)
            .await?;
        let epreuve = Epreuve::try_from(res)?;

        Ok(epreuve)
    }

    async fn insert(&self, data: &Epreuve) -> Result<i32> {
        let res = sqlx::query(
            "INSERT INTO epreuve (nom, fk_id_analyse, created_at) VALUES ($1, $2, $3) RETURNING id",
        )
        .bind(&data.nom)
        .bind(data.fk_id_analyse)
        .bind(data.created_at)
        .fetch_one(&self.pool)
        .await?;
        let id: i32 = res.try_get("id")?;
        Ok(id)
    }

    async fn update(&self, data: &Epreuve) -> Result<Epreuve> {
        let updated_at = chrono::Utc::now();
        let res = sqlx::query("UPDATE epreuve SET nom = $1, fk_id_analyse = $2, updated_at = $3 WHERE id = $4 RETURNING *")
            .bind(&data.nom)
            .bind(data.fk_id_analyse)
            .bind(updated_at)
            .bind(data.id)
            .fetch_one(&self.pool)
            .await?;

        let epreuve = Epreuve::try_from(res)?;

        Ok(epreuve)
    }

    async fn remove(&self, id: i32) -> Result<bool> {
        let res = sqlx::query("DELETE FROM epreuve WHERE id = $1")
            .bind(id)
            .execute(&self.pool)
            .await?;

        Ok(res.rows_affected() == 1)
    }

    async fn find_all(&self) -> Result<Vec<Epreuve>> {
        let res = sqlx::query("SELECT * FROM epreuve")
            .fetch_all(&self.pool)
            .await?;
        let mut epreuves: Vec<Epreuve> = Vec::new();
        for entry in res {
            epreuves.push(Epreuve::try_from(entry)?);
        }

        Ok(epreuves)
    }
}
