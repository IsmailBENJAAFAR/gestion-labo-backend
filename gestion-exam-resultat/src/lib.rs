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
        let mut mock: MockDao<Exam> = MockDao::new();
        let exam = Exam::new("Informatique".to_string(), 1);

        // Mocking the DAO
        mock.expect_find_all().times(1).returning(|| Ok(vec![]));
        mock.expect_insert().return_once(|_exam: Exam| Ok(true));
        {
            let exam = exam.clone();
            mock.expect_find_all()
                .times(1)
                .returning(move || Ok(vec![exam.clone()]));
        }
        mock.expect_find()
            .with(eq(2))
            .return_once(|_f| Err(anyhow!("error")));

        // Using the exams service with the MockDao object
        let (code, _, data) = services::get_exams(&mock).await;
        assert_eq!((code, data.as_str()), (StatusCode::OK, "[]"));

        let exam_dto = ExamDto {
            nom: String::from("Informatique"),
            fk_id_analyse: 1,
        };
        let (code, _) = services::create_exam(&mock, exam_dto).await;
        assert_eq!(code, StatusCode::CREATED);

        let (code, _, data) = services::get_exams(&mock).await;
        assert_eq!(
            (code, data),
            (StatusCode::OK, serde_json::to_string(&[exam]).unwrap())
        );

        let (code, _, _) = services::get_exam(&mock, 2).await;
        assert_eq!(StatusCode::NOT_FOUND, code);
    }
}
