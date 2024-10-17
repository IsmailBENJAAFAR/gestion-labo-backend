use axum::{extract::Path, Json};

use crate::dto::ExamDto;

pub async fn get_exams() -> String {
    "Exams get!".to_string()
}

pub async fn get_exam(Path(param): Path<i32>) -> String {
    format!("The path contained: {param}")
}

pub async fn create_exam(Json(data): Json<ExamDto>) -> String {
    println!("{data:?}");
    "Exam created".to_string()
}
