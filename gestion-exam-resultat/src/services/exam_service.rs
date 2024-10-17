use std::sync::Arc;

use anyhow::Result;
use axum::{http::StatusCode, response::IntoResponse};
use tokio::sync::OnceCell;

use crate::dao::Dao;
use crate::{dao::ExamDao, dto::ExamDto, models::Exam};

static EXAM_DAO: OnceCell<Arc<ExamDao>> = OnceCell::const_new();

pub async fn get_exam_dao() -> Arc<ExamDao> {
    EXAM_DAO
        .get_or_init(|| async { Arc::new(ExamDao::new()) })
        .await
        .clone()
}

pub async fn create_exam(exam: ExamDto) -> Result<impl IntoResponse> {
    let dao = get_exam_dao().await;
    let exam = Exam::new(exam.nom, exam.fk_id_analyse);
    if dao.insert(exam).await? {
        Ok((StatusCode::CREATED, "Exam has been created"))
    } else {
        Ok((StatusCode::BAD_REQUEST, "Exam hasn't been created"))
    }
}

pub async fn get_exams() -> Result<impl IntoResponse> {
    let dao = get_exam_dao().await;
    let exams = dao.find_all().await?;
    Ok(serde_json::to_string(&exams)?)
}
