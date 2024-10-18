mod controllers;
mod dao;
mod dto;
mod models;
mod services;

use anyhow::Result;
use axum::{
    http::StatusCode,
    response::IntoResponse,
    routing::{delete, get, post},
    Router,
};
use controllers::exam_controller;
use dao::ExamDao;
use dotenvy::dotenv;
use sqlx::postgres::PgPoolOptions;
use tokio::net::TcpListener;

#[derive(Clone)]
struct AppState {
    exam_dao: ExamDao,
}

#[tokio::main]
async fn main() -> Result<()> {
    dotenv()?;
    let global_router = Router::new().fallback(handler_404);
    let url = std::env::var("DATABASE_URL").unwrap();
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&url)
        .await
        .unwrap();

    let exam_dao = ExamDao::new(pool);
    let state = AppState { exam_dao };

    let app = global_router
        .nest(
            "/api",
            Router::new()
                .route("/exams", get(exam_controller::get_exams))
                .route("/exam", post(exam_controller::create_exam))
                .route("/exam/:id", get(exam_controller::get_exam))
                .route("/exam/:id", delete(exam_controller::delete_exam)),
        )
        .with_state(state);

    let listener = TcpListener::bind("127.0.0.1:8080").await.unwrap();
    axum::serve(listener, app).await.unwrap();
    Ok(())
}

async fn handler_404() -> impl IntoResponse {
    (StatusCode::NOT_FOUND, "no resource found.")
}
