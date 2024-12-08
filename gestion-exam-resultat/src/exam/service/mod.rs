use std::sync::Arc;

use anyhow::anyhow;
use axum::http::StatusCode;
use axum::Json;

use crate::dao::interface::Dao;

use super::api_error::ApiError;
use super::dto::ExamDto;
use super::model::Exam;

pub struct Service {
    pub dao: Arc<dyn Dao<Exam> + Sync + Send + 'static>,
}

impl Service {
    pub fn new(dao: Arc<dyn Dao<Exam> + Sync + Send + 'static>) -> Service {
        Service { dao }
    }

    pub async fn create_exam(&self, exam: ExamDto) -> Result<(StatusCode, Json<Exam>), ApiError> {
        let exam = Exam::new(
            exam.fk_num_dossier,
            exam.fk_id_epreuve,
            exam.fk_id_test_analyse,
        );
        match self.dao.insert(&exam).await {
            Ok(true) => Ok((StatusCode::CREATED, Json(exam))),
            // TODO: log the attempted object creation
            Ok(false) => Err(ApiError::with_status(
                anyhow!("Exam hasn't been created"),
                StatusCode::BAD_REQUEST,
            )),
            // TODO: log error
            Err(_e) => Err(ApiError::new(anyhow!("error: couldn't create exam"))),
        }
    }

    pub async fn get_exam(&self, id: i32) -> Result<(StatusCode, Json<Exam>), ApiError> {
        let exam = match self.dao.find(id).await {
            Ok(exam) => exam,
            // TODO: log error and return `exam not found` only
            Err(e) => Err(ApiError::with_status(
                anyhow!("exam not found: {e}"),
                StatusCode::NOT_FOUND,
            ))?,
        };
        Ok((StatusCode::OK, Json(exam)))
    }

    pub async fn get_exams(&self) -> Result<(StatusCode, Json<Vec<Exam>>), ApiError> {
        let exams = match self.dao.find_all().await {
            Ok(exams) => exams,
            Err(e) => Err(ApiError::new(e))?,
        };
        Ok((StatusCode::OK, Json(exams)))
    }

    pub async fn delete_exam(&self, id: i32) -> Result<StatusCode, ApiError> {
        match self.dao.remove(id).await {
            Ok(true) => Ok(StatusCode::NO_CONTENT),
            Ok(false) => Err(ApiError::with_status(
                anyhow!("error: examen has not been deleted"),
                StatusCode::BAD_REQUEST,
            ))?,
            // TODO: log errors
            Err(_e) => Err(ApiError::with_status(
                anyhow!("error: exam not found"),
                StatusCode::BAD_REQUEST,
            ))?,
        }
    }
}
