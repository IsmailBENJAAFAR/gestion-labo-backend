use std::sync::Arc;

use axum::{
    extract::{Path, State},
    response::IntoResponse,
    Json,
};
use tokio::sync::mpsc::Sender;

use crate::{epreuve::dto::CreateEpreuveDto, message_queue::QueueMessage};

use super::{dto::UpdateEpreuveDto, service};

pub async fn get_epreuves(State(service): State<Arc<service::Service>>) -> impl IntoResponse {
    service.get_epreuves().await
}

pub async fn get_epreuve(
    State(service): State<Arc<service::Service>>,
    Path(id): Path<i32>,
) -> impl IntoResponse {
    service.get_epreuve(id).await
}

pub async fn create_epreuve(
    State(service): State<Arc<service::Service>>,
    Json(data): Json<CreateEpreuveDto>,
) -> impl IntoResponse {
    service.create_epreuve(data).await
}

pub async fn update_epreuve(
    State(service): State<Arc<service::Service>>,
    Path(id): Path<i32>,
    Json(data): Json<UpdateEpreuveDto>,
) -> impl IntoResponse {
    service.update_epreuve(id, data).await
}

pub async fn delete_epreuve(
    State(service): State<Arc<service::Service>>,
    State(queue): State<Arc<Sender<QueueMessage>>>,
    Path(id): Path<i32>,
) -> impl IntoResponse {
    service.delete_epreuve(id, queue).await
}
