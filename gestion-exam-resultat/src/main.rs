mod controllers;
mod dao;
mod dto;
mod models;
mod services;

use anyhow::{Context, Result};
use axum::{extract::OriginalUri, http::StatusCode, response::IntoResponse, routing::get, Router};
use controllers::exam_controller;
use dao::ExamDao;
use dotenvy::dotenv;
use sqlx::postgres::PgPoolOptions;
use tokio::net::TcpListener;
use tower_http::cors::CorsLayer;

#[derive(Clone)]
struct AppState {
    exam_dao: ExamDao,
}

#[tokio::main]
async fn main() -> Result<()> {
    match dotenv() {
        Ok(_) => eprintln!(".env file loaded successfully."),
        Err(_) => eprintln!("Warning: .env file not found."),
    };
    let global_router = Router::new().fallback(handler_404);
    let url = std::env::var("DATABASE_URL").context("Please set DATABASE_URL for database")?;
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&url)
        .await
        .context("can't connect to database")?;

    let exam_dao = ExamDao::new(pool);
    let state = AppState { exam_dao };

    let app = global_router
        .nest(
            "/api",
            Router::new()
                .route(
                    "/exam",
                    get(exam_controller::get_exams).post(exam_controller::create_exam),
                )
                .route(
                    "/exam/:id",
                    get(exam_controller::get_exam).delete(exam_controller::delete_exam),
                ),
        )
        .layer(CorsLayer::permissive())
        .with_state(state);

    let listener = TcpListener::bind("0.0.0.0:80").await?;
    axum::serve(listener, app).await?;
    Ok(())
}

async fn handler_404(OriginalUri(uri): OriginalUri) -> impl IntoResponse {
    (
        StatusCode::NOT_FOUND,
        format!("no resource found in {uri:?}"),
    )
}
