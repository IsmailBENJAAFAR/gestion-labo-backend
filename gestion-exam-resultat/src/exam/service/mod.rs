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

    pub async fn create_exam(&self, exam: ExamDto) -> (StatusCode, String) {
        let exam = Exam::new(
            exam.fk_num_dossier,
            exam.fk_id_epreuve,
            exam.fk_id_test_analyse,
        );
        match self.dao.insert(exam).await {
            Ok(true) => (
                StatusCode::CREATED,
                Json("Exam has been created").to_string(),
            ),
            Ok(false) => (
                StatusCode::BAD_REQUEST,
                Json("Exam hasn't been created").to_string(),
            ),
            Err(e) => (
                StatusCode::BAD_REQUEST,
                Json(format!("error: {e:?}")).to_string(),
            ),
        }
    }

    pub async fn get_exam(&self, id: i32) -> Result<(StatusCode, Json<Exam>), ApiError> {
        let exam = match self.dao.find(id).await {
            Ok(exam) => exam,
            Err(e) => Err(ApiError::new(
                anyhow!("exam not found: {e}"),
                Some(StatusCode::NOT_FOUND),
            ))?,
        };
        Ok((StatusCode::OK, Json(exam)))
    }

    pub async fn get_exams(&self) -> Result<(StatusCode, Json<Vec<Exam>>), ApiError> {
        let exams = match self.dao.find_all().await {
            Ok(exams) => exams,
            Err(e) => Err(ApiError::new(e, None))?,
        };
        Ok((StatusCode::OK, Json(exams)))
    }

    pub async fn delete_exam(&self, id: i32) -> Result<StatusCode, ApiError> {
        match self.dao.remove(id).await {
            Ok(true) => Ok(StatusCode::NO_CONTENT),
            Ok(false) => Err(ApiError::new(
                anyhow!("error: examen has not been deleted"),
                Some(StatusCode::BAD_REQUEST),
            ))?,
            Err(e) => Err(ApiError::new(
                anyhow!("error: exam not found: {e}"),
                Some(StatusCode::BAD_REQUEST),
            ))?,
        }
    }
}
