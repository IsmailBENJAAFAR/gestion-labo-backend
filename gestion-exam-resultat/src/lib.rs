mod dao;
mod dto;
mod models;
mod services;

#[cfg(test)]
mod test {
    use anyhow::anyhow;
    use axum::http::StatusCode;
    use mockall::predicate::eq;

    use crate::{dao::MockDao, dto::ExamDto, models::Exam, services};

    #[tokio::test]
    async fn test_exam_service() {
        let exam = Exam::new("Informatique".to_string(), 1);
        let mut mock: MockDao<Exam> = MockDao::new();

        // Mocking the DAO
        mock.expect_find_all().return_once(|| Ok(vec![]));
        mock.expect_insert().return_once(|_exam: Exam| Ok(true));
        mock.expect_find_all().return_once(|| Ok(vec![exam]));
        mock.expect_find()
            .with(eq(2))
            .return_once(|_f| Err(anyhow!("error")));

        // Using the exams service with the MockDao object
        let (code, _, resp) = services::get_exams(&mock).await;
        assert_eq!((code, resp.as_str()), (StatusCode::OK, "[]"));

        let exam_dto = ExamDto {
            nom: String::from("Informatique"),
            fk_id_analyse: 1,
        };
        let (code, _) = services::create_exam(&mock, exam_dto).await;
        assert_eq!(code, StatusCode::CREATED);
    }
}
