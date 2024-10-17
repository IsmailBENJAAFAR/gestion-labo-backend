use std::sync::Arc;

use axum::{http::StatusCode, response::IntoResponse};
use tokio::sync::OnceCell;

use crate::dao::Dao;
use crate::{dao::ExamDao, dto::ExamDto, models::Exam};

static EXAM_DAO: OnceCell<Arc<ExamDao>> = OnceCell::const_new();

async fn get_exam_dao() -> Arc<ExamDao> {
    EXAM_DAO
        .get_or_init(|| async { Arc::new(ExamDao::new()) })
        .await
        .clone()
}

async fn create_post(exam: ExamDto) -> impl IntoResponse {
    let exam = Exam::new(exam.nom, exam.fk_id_analyse);
    let dao = get_exam_dao().await;
    if dao.insert(exam) {
        (StatusCode::CREATED, "Exam has been created")
    } else {
        (StatusCode::BAD_REQUEST, "Exam hasn't been created")
    }
}
