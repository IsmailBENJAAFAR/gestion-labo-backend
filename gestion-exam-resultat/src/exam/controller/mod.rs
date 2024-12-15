use std::sync::Arc;

use axum::{
    extract::{Path, State},
    response::IntoResponse,
    Json,
};
use tokio::sync::mpsc::Sender;

use crate::{exam::dto::ExamDto, message_queue::QueueMessage};

use super::service;

pub async fn get_exams(State(service): State<Arc<service::Service>>) -> impl IntoResponse {
    service.get_exams().await
}

pub async fn get_exam(
    State(service): State<Arc<service::Service>>,
    Path(id): Path<i32>,
) -> impl IntoResponse {
    service.get_exam(id).await
}

pub async fn create_exam(
    State(service): State<Arc<service::Service>>,
    Json(data): Json<ExamDto>,
) -> impl IntoResponse {
    service.create_exam(data).await
}

pub async fn delete_exam(
    State(service): State<Arc<service::Service>>,
    State(queue): State<Arc<Sender<QueueMessage>>>,
    Path(id): Path<i32>,
) -> impl IntoResponse {
    service.delete_exam(id, queue).await
}
