use axum::{
    extract::{Path, State},
    response::IntoResponse,
    Json,
};

use crate::{dto::ExamDto, services, AppState};

pub async fn get_exams(State(state): State<AppState>) -> impl IntoResponse {
    services::get_exams(&state.exam_dao).await
}

pub async fn get_exam(State(state): State<AppState>, Path(id): Path<i32>) -> impl IntoResponse {
    services::get_exam(&state.exam_dao, id).await
}

pub async fn create_exam(
    State(state): State<AppState>,
    Json(data): Json<ExamDto>,
) -> impl IntoResponse {
    services::create_exam(&state.exam_dao, data).await
}

pub async fn delete_exam(State(state): State<AppState>, Path(id): Path<i32>) -> impl IntoResponse {
    services::delete_exam(&state.exam_dao, id).await;
}
