use anyhow::Result;
use axum::{extract::Path, response::IntoResponse, Json};

use crate::{dto::ExamDto, services};

pub async fn get_exams() -> Result<impl IntoResponse> {
    Ok(services::get_exams().await?)
}

pub async fn get_exam(Path(param): Path<i32>) -> String {
    format!("The path contained: {param}")
}

pub async fn create_exam(Json(data): Json<ExamDto>) -> Result<impl IntoResponse> {
    Ok(services::create_exam(data).await?)
}
