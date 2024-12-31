use std::sync::Arc;

use axum::{
    extract::{Path, State},
    response::IntoResponse,
    Json,
};
use tokio::sync::mpsc::Sender;

use crate::{resultat::dto::CreateResultatDto, message_queue::QueueMessage};

use super::{dto::UpdateResultatDto, service};

pub async fn get_resultats(State(service): State<Arc<service::Service>>) -> impl IntoResponse {
    service.get_resultats().await
}

pub async fn get_resultat(
    State(service): State<Arc<service::Service>>,
    Path(id): Path<i32>,
) -> impl IntoResponse {
    service.get_resultat(id).await
}

pub async fn create_resultat(
    State(service): State<Arc<service::Service>>,
    Json(data): Json<CreateResultatDto>,
) -> impl IntoResponse {
    service.create_resultat(data).await
}

pub async fn update_resultat(
    State(service): State<Arc<service::Service>>,
    Path(id): Path<i32>,
    Json(data): Json<UpdateResultatDto>,
) -> impl IntoResponse {
    service.update_resultat(id, data).await
}

pub async fn delete_resultat(
    State(service): State<Arc<service::Service>>,
    State(queue): State<Arc<Sender<QueueMessage>>>,
    Path(id): Path<i32>,
) -> impl IntoResponse {
    service.delete_resultat(id, queue).await
}
