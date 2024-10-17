mod controllers;
mod dao;
mod models;
mod services;
use anyhow::Result;
use axum::{
    http::StatusCode,
    response::IntoResponse,
    routing::{get, post},
    Router,
};
use controllers::exam_controller;
use tokio::net::TcpListener;

#[tokio::main]
async fn main() -> Result<()> {
    let global_router = Router::new().fallback(handler_404);
    let app = global_router.nest(
        "/api",
        Router::new()
            .route("/exams", get(exam_controller::get_exams))
            .route("/exam", post(exam_controller::create_exam))
            .route("/exam/:id", get(exam_controller::get_exam)),
    );

    let listener = TcpListener::bind("127.0.0.1:8080").await.unwrap();
    axum::serve(listener, app).await.unwrap();
    Ok(())
}

async fn handler_404() -> impl IntoResponse {
    (StatusCode::NOT_FOUND, "no resource found.")
}
