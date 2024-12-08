pub mod dao;
pub mod exam;

#[cfg(test)]
mod test {
    use std::sync::Arc;

    use anyhow::{anyhow, Context, Result};
    use axum::{http::StatusCode, Json};
    use mockall::predicate::eq;
    use sqlx::postgres::PgPoolOptions;
    use testcontainers::{
        core::{IntoContainerPort, WaitFor},
        runners::AsyncRunner,
        GenericImage, ImageExt,
    };
    use tracing::info;

    use crate::{
        dao::interface::{Dao, MockDao},
        exam::{api_error::ApiError, dao::ExamDao, dto::ExamDto, model::Exam, service},
    };

    #[tokio::test]
    async fn test_exam_database() -> Result<()> {
        info!("Starting database container");
        const PORT: u16 = 5432;
        const USER: &'static str = "user";
        const PASSWORD: &'static str = "mypassword";

        let container = GenericImage::new("postgres", "17.0")
            .with_exposed_port(PORT.tcp())
            .with_wait_for(WaitFor::message_on_stderr("ready to accept connections"))
            .with_network("bridge")
            .with_env_var("POSTGRES_USER", USER)
            .with_env_var("POSTGRES_PASSWORD", PASSWORD)
            .start()
            .await
            .expect("Postgres database container didn't start.");
        info!("Database container started");

        let host = container.get_host().await?;
        let host_port = container.get_host_port_ipv4(PORT).await?;

        info!("host: {host}");
        info!("host_port: {host_port}");

        let url = format!("postgres://{USER}:{PASSWORD}@{host}:{host_port}");
        let pool = PgPoolOptions::new()
            .max_connections(5)
            .connect(&url)
            .await
            .context("can't connect to database")?;
        sqlx::migrate!("./migrations/").run(&pool).await?;

        info!("Database migration successful");

        let url_db = format!("{url}/user");

        let pool = PgPoolOptions::new()
            .max_connections(5)
            .connect(&url_db)
            .await
            .context("can't connect to database")?;

        let dao = ExamDao::new(pool);

        let res = dao.find(1).await;
        assert!(res.is_err());

        let res = dao.insert(Exam::new(1, 2, 3)).await.context("insert")?;
        assert!(res);

        let res = dao.find(1).await?;
        assert_eq!(
            (
                res.fk_num_dossier,
                res.fk_id_epreuve,
                res.fk_id_test_analyse
            ),
            (1, 2, 3)
        );

        let res = dao.find_all().await?;
        assert_eq!(res.len(), 1);
        let res = &res[0];
        assert_eq!(
            (
                res.fk_num_dossier,
                res.fk_id_epreuve,
                res.fk_id_test_analyse
            ),
            (1, 2, 3)
        );

        let res = dao.remove(1).await?;
        assert!(res);

        let res = dao.find_all().await?;
        assert_eq!(res.len(), 0);

        let res = dao.find(1).await;
        assert!(res.is_err());

        Ok(())
    }

    #[tokio::test]
    async fn test_exam_service() {
        let mut mock: MockDao<Exam> = MockDao::new();
        let exam = Exam::new(1, 1, 1);

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

        let service = service::Service::new(Arc::new(mock));
        // Using the exams service with the MockDao object
        let (code, Json(data)) = service.get_exams().await.unwrap();
        assert_eq!((code, data.len()), (StatusCode::OK, 0));

        let exam_dto = ExamDto::new(1, 1, 1);
        let (code, _) = service.create_exam(exam_dto).await;
        assert_eq!(code, StatusCode::CREATED);

        let (code, Json(data)) = service.get_exams().await.unwrap();
        assert_eq!(
            (code, serde_json::to_string(&data).unwrap()),
            (StatusCode::OK, serde_json::to_string(&[exam]).unwrap())
        );

        if let Err(ApiError {
            status: Some(status),
            ..
        }) = service.get_exam(2).await
        {
            assert_eq!(StatusCode::NOT_FOUND, status);
        } else {
            panic!("service.get_exam failed");
        }
    }
}
