use std::sync::Arc;

use anyhow::anyhow;
use axum::http::StatusCode;
use axum::Json;
use tokio::sync::mpsc::Sender;

use crate::dao::interface::Dao;
use crate::message_queue::{EventType::Point, QueueMessage};

use super::api_error::ApiError;
use super::dto::{CreateEpreuveDto, UpdateEpreuveDto};
use super::model::Epreuve;

pub struct Service {
    pub dao: Arc<dyn Dao<Epreuve> + Sync + Send + 'static>,
}

impl Service {
    pub fn new(dao: Arc<dyn Dao<Epreuve> + Sync + Send + 'static>) -> Service {
        Service { dao }
    }

    pub async fn create_epreuve(
        &self,
        epreuve: CreateEpreuveDto,
    ) -> Result<(StatusCode, Json<Epreuve>), ApiError> {
        let epreuve = Epreuve::new(&epreuve.nom, epreuve.fk_id_analyse);
        match self.dao.insert(&epreuve).await {
            Ok(id) => Ok((StatusCode::CREATED, Json(Epreuve { id, ..epreuve }))),
            Err(e) => {
                tracing::error!("epreuve wasn't created: {epreuve:?}, error: {e}");
                Err(ApiError::new(anyhow!("error: couldn't create epreuve")))
            }
        }
    }

    pub async fn get_epreuve(&self, id: i32) -> Result<(StatusCode, Json<Epreuve>), ApiError> {
        let epreuve = match self.dao.find(id).await {
            Ok(epreuve) => epreuve,
            Err(e) => {
                tracing::error!("epreuve not found with id: {id}, error: {e}");
                Err(ApiError::with_status(
                    anyhow!("epreuve not found with id: {id}"),
                    StatusCode::NOT_FOUND,
                ))?
            }
        };
        Ok((StatusCode::OK, Json(epreuve)))
    }

    pub async fn get_epreuves(&self) -> Result<(StatusCode, Json<Vec<Epreuve>>), ApiError> {
        let epreuves = match self.dao.find_all().await {
            Ok(epreuves) => epreuves,
            Err(e) => {
                tracing::error!("couldn't fetch epreuves: {e}");
                Err(ApiError::new(anyhow!("couldn't fetch epreuves")))?
            }
        };
        Ok((StatusCode::OK, Json(epreuves)))
    }

    pub async fn update_epreuve(
        &self,
        id: i32,
        epreuve_dto: UpdateEpreuveDto,
    ) -> Result<(StatusCode, Json<Epreuve>), ApiError> {
        let (_, Json(epreuve)) = self.get_epreuve(id).await?;
        let updated_epreuve = Epreuve::with_id(
            id,
            &epreuve_dto.nom.unwrap_or(epreuve.nom),
            epreuve_dto.fk_id_analyse.unwrap_or(epreuve.fk_id_analyse),
        );

        match self.dao.update(&updated_epreuve).await {
            Ok(epreuve) => Ok((StatusCode::OK, Json(epreuve))),
            Err(e) => {
                tracing::error!("epreuve with id: {id}, hasn't been updated: {e}");
                Err(ApiError::with_status(
                    anyhow!("epreuve not found"),
                    StatusCode::BAD_REQUEST,
                ))
            }
        }
    }

    pub async fn delete_epreuve(
        &self,
        id: i32,
        queue: Arc<Sender<QueueMessage>>,
    ) -> Result<StatusCode, ApiError> {
        match self.dao.remove(id).await {
            Ok(true) => {
                let queue_message = QueueMessage {
                    msg_type: Point,
                    message: "delete epreuve".into(),
                };
                if let Err(e) = queue.send(queue_message).await {
                    tracing::error!("{e}")
                }
                Ok(StatusCode::NO_CONTENT)
            }
            Ok(false) => {
                tracing::error!("attempt to delete epreuve with id: {id}");
                Err(ApiError::with_status(
                    anyhow!("error: epreuveen has not been deleted"),
                    StatusCode::BAD_REQUEST,
                ))?
            }
            Err(e) => {
                tracing::error!("failed to delete epreuve with id: {id}, error: {e}");
                Err(ApiError::with_status(
                    anyhow!("error: epreuve not found"),
                    StatusCode::BAD_REQUEST,
                ))?
            }
        }
    }
}
