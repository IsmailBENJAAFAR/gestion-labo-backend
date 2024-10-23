use axum::{
    extract::{Path, State},
    response::IntoResponse,
    Json,
};

use crate::exam::dto::ExamDto;

use super::{dao::ExamDao, service};

pub async fn get_exams(State(exam_dao): State<ExamDao>) -> impl IntoResponse {
    service::get_exams(&exam_dao).await
}

pub async fn get_exam(State(exam_dao): State<ExamDao>, Path(id): Path<i32>) -> impl IntoResponse {
    service::get_exam(&exam_dao, id).await
}

pub async fn create_exam(
    State(exam_dao): State<ExamDao>,
    Json(data): Json<ExamDto>,
) -> impl IntoResponse {
    service::create_exam(&exam_dao, data).await
}

pub async fn delete_exam(
    State(exam_dao): State<ExamDao>,
    Path(id): Path<i32>,
) -> impl IntoResponse {
    service::delete_exam(&exam_dao, id).await;
}
