mod dao;
mod exam;

use std::sync::Arc;

use anyhow::{Context, Result};
use axum::{
    extract::{FromRef, OriginalUri},
    http::StatusCode,
    response::IntoResponse,
    routing::get,
    Router,
};
use dotenvy::dotenv;
use exam::dao::ExamDao;
use sqlx::postgres::PgPoolOptions;
use tokio::net::TcpListener;
use tower_http::cors::CorsLayer;

#[derive(Clone)]
struct AppState {
    exam_service: Arc<exam::service::Service>,
}

impl FromRef<AppState> for Arc<exam::service::Service> {
    fn from_ref(input: &AppState) -> Self {
        input.exam_service.clone()
    }
}

#[tokio::main]
async fn main() -> Result<()> {
    tracing_subscriber::fmt().init();

    match dotenv() {
        Ok(_) => eprintln!(".env file loaded successfully."),
        Err(_) => eprintln!("Warning: .env file not found."),
    };
    let url = std::env::var("DATABASE_URL").context("Please set DATABASE_URL for database")?;
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&url)
        .await
        .context("can't connect to database")?;

    let exam_dao = ExamDao::new(pool);
    let exam_service = Arc::new(exam::service::Service::new(Arc::new(exam_dao)));
    let state = AppState { exam_service };

    let app = app(state);

    let listener = TcpListener::bind("0.0.0.0:8080")
        .await
        .context("Binding listener to address")?;
    axum::serve(listener, app).await?;
    Ok(())
}

fn app(state: AppState) -> Router {
    Router::new()
        .fallback(handler_404)
        .nest(
            "/api/v1",
            Router::new()
                .route(
                    "/examens",
                    get(exam::controller::get_exams).post(exam::controller::create_exam),
                )
                .route(
                    "/examens/:id",
                    get(exam::controller::get_exam).delete(exam::controller::delete_exam),
                ),
        )
        .layer(CorsLayer::permissive())
        .with_state(state)
}

async fn handler_404(OriginalUri(uri): OriginalUri) -> impl IntoResponse {
    (
        StatusCode::NOT_FOUND,
        format!("no resource found in {uri:?}"),
    )
}
