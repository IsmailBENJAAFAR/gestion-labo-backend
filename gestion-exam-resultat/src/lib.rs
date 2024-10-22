mod dao;
mod dto;
mod models;
mod services;

#[cfg(test)]
mod test {
    use crate::{dao::MockDao, dto::ExamDto, models::Exam, services};
    use anyhow::{anyhow, Context, Result};
    use axum::http::StatusCode;
    use mockall::predicate::eq;
    use sqlx::postgres::PgPoolOptions;
    use testcontainers::{
        core::{IntoContainerPort, WaitFor},
        runners::AsyncRunner,
        GenericImage, ImageExt,
    };
    use tracing::info;

    #[tokio::test]
    async fn test_exam_database() -> Result<()> {
        info!("Starting database container");
        const PORT: u16 = 5432;
        let container = GenericImage::new("postgres", "17.0")
            .with_exposed_port(PORT.tcp())
            .with_wait_for(WaitFor::message_on_stderr("ready to accept connections"))
            .with_network("bridge")
            .with_env_var("POSTGRES_USER", "user")
            .with_env_var("POSTGRES_PASSWORD", "mysecretpassword")
            .start()
            .await
            .expect("Postgres database container didn't start.");

        info!("Database container started");
        let host = container.get_host().await?;
        let host_port = container.get_host_port_ipv4(PORT).await?;
        info!("host: {host}");
        info!("host_port: {host_port}");

        let url = format!("postgres://user:mysecretpassword@{host}:{host_port}");
        let pool = PgPoolOptions::new()
            .max_connections(5)
            .connect(&url)
            .await
            .context("can't connect to database")?;
        sqlx::migrate!("./migrations/").run(&pool).await?;

        let url_db = format!("{url}/user");

        let pool = PgPoolOptions::new()
            .max_connections(5)
            .connect(&url_db)
            .await
            .context("can't connect to database")?;

        info!("Database and migration successful");
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

        // Using the exams service with the MockDao object
        let (code, _, data) = services::get_exams(&mock).await;
        assert_eq!((code, data.as_str()), (StatusCode::OK, "[]"));

        let exam_dto = ExamDto::new(1, 1, 1);
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
