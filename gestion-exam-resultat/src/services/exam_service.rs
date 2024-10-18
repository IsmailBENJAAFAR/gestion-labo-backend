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

pub async fn get_exams(dao: ExamDao) -> impl IntoResponse {
    let exams = match dao.find_all().await {
        Ok(exams) => exams,
        Err(e) => return (StatusCode::BAD_REQUEST, format!("error: {e:?}")),
    };
    let response = match serde_json::to_string(&exams) {
        Ok(res) => res,
        Err(e) => return (StatusCode::BAD_REQUEST, format!("error: {e:?}")),
    };

    (StatusCode::OK, response)
}
