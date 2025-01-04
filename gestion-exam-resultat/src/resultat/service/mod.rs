use super::dto::{CreateResultatDto, UpdateResultatDto};
use super::model::Resultat;
use crate::api_error::ApiError;
use crate::dao::interface::Dao;
use crate::message_queue::{EventType, QueueMessage};
use anyhow::anyhow;
use axum::http::StatusCode;
use axum::Json;
use std::sync::Arc;
use tokio::sync::mpsc::Sender;

pub struct Service {
    pub dao: Arc<dyn Dao<Resultat> + Sync + Send + 'static>,
}

impl Service {
    pub fn new(dao: Arc<dyn Dao<Resultat> + Sync + Send + 'static>) -> Service {
        Service { dao }
    }

    pub async fn create_resultat(
        &self,
        resultat: CreateResultatDto,
    ) -> Result<(StatusCode, Json<Resultat>), ApiError> {
        let resultat = Resultat::new(resultat.fk_id_exam, &resultat.observation, resultat.score);
        match self.dao.insert(&resultat).await {
            Ok(id) => Ok((StatusCode::CREATED, Json(Resultat { id, ..resultat }))),
            Err(e) => {
                tracing::error!("resultat wasn't created: {resultat:?}, error: {e}");
                Err(ApiError::new(anyhow!("error: couldn't create resultat")))
            }
        }
    }

    pub async fn get_resultat(&self, id: i32) -> Result<(StatusCode, Json<Resultat>), ApiError> {
        let resultat = match self.dao.find(id).await {
            Ok(resultat) => resultat,
            Err(e) => {
                tracing::error!("resultat not found with id: {id}, error: {e}");
                Err(ApiError::with_status(
                    anyhow!("resultat not found with id: {id}"),
                    StatusCode::NOT_FOUND,
                ))?
            }
        };
        Ok((StatusCode::OK, Json(resultat)))
    }

    pub async fn get_resultats(&self) -> Result<(StatusCode, Json<Vec<Resultat>>), ApiError> {
        let resultats = match self.dao.find_all().await {
            Ok(resultats) => resultats,
            Err(e) => {
                tracing::error!("couldn't fetch resultats: {e}");
                Err(ApiError::new(anyhow!("couldn't fetch resultats")))?
            }
        };
        Ok((StatusCode::OK, Json(resultats)))
    }

    pub async fn update_resultat(
        &self,
        id: i32,
        resultat_dto: UpdateResultatDto,
    ) -> Result<(StatusCode, Json<Resultat>), ApiError> {
        let (_, Json(resultat)) = self.get_resultat(id).await?;
        let updated_resultat = Resultat::with_id(
            id,
            resultat_dto.fk_id_exam.unwrap_or(resultat.fk_id_exam),
            &resultat_dto.observation.unwrap_or(resultat.observation),
            resultat_dto.score.unwrap_or(resultat.score),
        );

        match self.dao.update(&updated_resultat).await {
            Ok(resultat) => Ok((StatusCode::OK, Json(resultat))),
            Err(e) => {
                tracing::error!("resultat with id: {id}, hasn't been updated: {e}");
                Err(ApiError::with_status(
                    anyhow!("resultat not found"),
                    StatusCode::BAD_REQUEST,
                ))
            }
        }
    }

    pub async fn delete_resultat(
        &self,
        id: i32,
        queue: Arc<Sender<QueueMessage>>,
    ) -> Result<StatusCode, ApiError> {
        match self.dao.remove(id).await {
            Ok(true) => {
                let queue_message = QueueMessage {
                    destination: EventType::Topic("resultaten.deleted".to_string()),
                    message: format!("resultat with id: {id} has been deleted"),
                };
                if let Err(e) = queue.send(queue_message).await {
                    tracing::error!("{e}")
                }
                Ok(StatusCode::NO_CONTENT)
            }
            Ok(false) => {
                tracing::error!("attempt to delete resultat with id: {id}");
                Err(ApiError::with_status(
                    anyhow!("error: resultaten has not been deleted"),
                    StatusCode::BAD_REQUEST,
                ))?
            }
            Err(e) => {
                tracing::error!("failed to delete resultat with id: {id}, error: {e}");
                Err(ApiError::with_status(
                    anyhow!("error: resultat not found"),
                    StatusCode::BAD_REQUEST,
                ))?
            }
        }
    }
}
