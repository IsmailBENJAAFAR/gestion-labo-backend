use ::chrono::{DateTime, Utc};
use anyhow::Result;
use axum::{routing::get, Router};
use dotenvy::dotenv;
use sqlx::{
    postgres::{PgPoolOptions, PgRow},
    types::chrono,
    Row,
};
use tokio::net::TcpListener;

struct Exam {
    id: i32,
    nom: String,
    created_at: chrono::DateTime<chrono::Utc>,
    fk_id_analyse: i32,
}

async fn hello() -> &'static str {
    "This is the microservice for exams and results"
}

#[tokio::main]
async fn main() -> Result<()> {
    dotenv()?;
    let url = std::env::var("DATABASE_URL")?;
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&url)
        .await?;

    // let row = sqlx::query("INSERT INTO exam (nom, created_at) VALUES ($1, $2)")
    //     .bind("Info")
    //     .bind(Utc::now())
    //     .execute(&pool)
    //     .await?;
    //
    // let rows_affected = row.rows_affected();
    // println!("Rows affected: {}", rows_affected);

    let rows = sqlx::query("SELECT * FROM exam").fetch_all(&pool).await?;

    for row in rows {
        let id: i32 = row.try_get("id")?;
        let nom: String = row.try_get("nom")?;
        let created_at: DateTime<Utc> = row.try_get("created_at")?;

        println!("id: {id}, nom: {nom}, created_at: {created_at:?}");
    }
    //
    // let app = Router::new().route("/hello", get(hello));
    //
    // let listener = TcpListener::bind("127.0.0.1:8080").await.unwrap();
    // axum::serve(listener, app).await.unwrap();
    Ok(())
}
