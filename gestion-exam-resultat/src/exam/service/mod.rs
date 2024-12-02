use std::sync::Arc;

use axum::http::{header, HeaderName};
use axum::Json;
use axum::{http::StatusCode, response::IntoResponse};

use crate::dao::interface::Dao;

use super::dto::ExamDto;
use super::model::Exam;

pub struct Service {
    pub dao: Arc<dyn Dao<Exam> + Sync + Send + 'static>,
}

impl Service {
    pub fn new(dao: Arc<dyn Dao<Exam> + Sync + Send + 'static>) -> Service {
        Service { dao }
    }

    pub async fn create_exam(&self, exam: ExamDto) -> (StatusCode, std::string::String) {
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

    // TODO: Transform all serde_json calls to axum::Json instead

    pub async fn get_exam(
        &self,
        id: i32,
    ) -> (
        StatusCode,
        [(HeaderName, &'static str); 1],
        std::string::String,
    ) {
        let exam = match self.dao.find(id).await {
            Ok(exam) => exam,
            Err(e) => {
                return (
                    StatusCode::NOT_FOUND,
                    [(header::CONTENT_TYPE, "application/json")],
                    format!("error: exam not found: {e:?}"),
                )
            }
        };
        match serde_json::to_string(&exam) {
            Ok(response) => (
                StatusCode::OK,
                [(header::CONTENT_TYPE, "application/json")],
                response,
            ),
            Err(e) => (
                StatusCode::INTERNAL_SERVER_ERROR,
                [(header::CONTENT_TYPE, "application/json")],
                format!("error: {e:?}"),
            ),
        }
    }

    pub async fn get_exams(
        &self,
    ) -> (
        StatusCode,
        [(HeaderName, &'static str); 1],
        std::string::String,
    ) {
        let exams = match self.dao.find_all().await {
            Ok(exams) => exams,
            Err(e) => {
                return (
                    StatusCode::BAD_REQUEST,
                    [(header::CONTENT_TYPE, "application/json")],
                    format!("error: {e:?}"),
                )
            }
        };
        match serde_json::to_string(&exams) {
            Ok(response) => (
                StatusCode::OK,
                [(header::CONTENT_TYPE, "application/json")],
                response,
            ),
            Err(e) => (
                StatusCode::BAD_REQUEST,
                [(header::CONTENT_TYPE, "application/json")],
                format!("error: {e:?}"),
            ),
        }
    }

    pub async fn delete_exam(&self, id: i32) -> impl IntoResponse {
        match self.dao.remove(id).await {
            Ok(true) => (StatusCode::NO_CONTENT, "Exam has been deleted.".to_string()),
            Ok(false) => (
                StatusCode::BAD_REQUEST,
                "Exam has not been deleted.".to_string(),
            ),
            Err(e) => (
                StatusCode::BAD_REQUEST,
                format!("error: exam not found: {e:?}"),
            ),
        }
    }
}
