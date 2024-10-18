use axum::http::header;
use axum::{http::StatusCode, response::IntoResponse};

use crate::dao::Dao;
use crate::{dao::ExamDao, dto::ExamDto, models::Exam};

pub async fn create_exam(dao: ExamDao, exam: ExamDto) -> impl IntoResponse {
    let exam = Exam::new(exam.nom, exam.fk_id_analyse);
    let res = match dao.insert(exam).await {
        Ok(res) => res,
        Err(e) => return (StatusCode::BAD_REQUEST, format!("error: {e:?}")),
    };
    if res {
        (StatusCode::CREATED, "Exam has been created".to_string())
    } else {
        (
            StatusCode::BAD_REQUEST,
            "Exam hasn't been created".to_string(),
        )
    }
}

pub async fn get_exam(dao: ExamDao, id: i32) -> impl IntoResponse {
    let exam = match dao.find(id).await {
        Ok(exam) => exam,
        Err(e) => {
            return (
                StatusCode::NOT_FOUND,
                [(header::CONTENT_TYPE, "application/json")],
                format!("error: exam not found: {e:?}"),
            )
        }
    };
    let response = match serde_json::to_string(&exam) {
        Ok(res) => res,
        Err(e) => {
            return (
                StatusCode::INTERNAL_SERVER_ERROR,
                [(header::CONTENT_TYPE, "application/json")],
                format!("error: {e:?}"),
            )
        }
    };

    (
        StatusCode::OK,
        [(header::CONTENT_TYPE, "application/json")],
        response,
    )
}

pub async fn get_exams(dao: ExamDao) -> impl IntoResponse {
    let exams = match dao.find_all().await {
        Ok(exams) => exams,
        Err(e) => {
            return (
                StatusCode::BAD_REQUEST,
                [(header::CONTENT_TYPE, "application/json")],
                format!("error: {e:?}"),
            )
        }
    };
    let response = match serde_json::to_string(&exams) {
        Ok(res) => res,
        Err(e) => {
            return (
                StatusCode::BAD_REQUEST,
                [(header::CONTENT_TYPE, "application/json")],
                format!("error: {e:?}"),
            )
        }
    };

    (
        StatusCode::OK,
        [(header::CONTENT_TYPE, "application/json")],
        response,
    )
}

pub async fn delete_exam(dao: ExamDao, id: i32) -> impl IntoResponse {
    let res = match dao.remove(id).await {
        Ok(res) => res,
        Err(e) => {
            return (
                StatusCode::BAD_REQUEST,
                format!("error: exam not found: {e:?}"),
            )
        }
    };

    if res {
        (StatusCode::NO_CONTENT, "Exam has been deleted.".to_string())
    } else {
        (
            StatusCode::BAD_REQUEST,
            "Exam has not been deleted.".to_string(),
        )
    }
}
