use anyhow::Context;
use axum::{
    extract::{FromRef, OriginalUri},
    http::StatusCode,
    response::IntoResponse,
    routing::get,
    Router,
};
use dotenvy::dotenv;
use exam::dao::ExamDao;
use sqlx::postgres::PgPoolOptions;
use std::sync::Arc;
use tokio::net::TcpListener;
use tower_http::cors::CorsLayer;

mod dao;
mod exam;

#[derive(Clone)]
pub struct AppState {
    pub exam_service: Arc<exam::service::Service>,
}

impl FromRef<AppState> for Arc<exam::service::Service> {
    fn from_ref(input: &AppState) -> Self {
        input.exam_service.clone()
    }
}

pub async fn run_app() -> anyhow::Result<()> {
    tracing_subscriber::fmt().init();

    match dotenv() {
        Ok(f) => eprintln!(".env file: {f:?} loaded successfully."),
        Err(_) => eprintln!("Warning: .env file not found."),
    };
    let url = std::env::var("DATABASE_URL").context("Please set DATABASE_URL for database")?;
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&url)
        .await
        .context("can't connect to database")?;

    let exam_dao = ExamDao::new(pool);
    let exam_service = Arc::new(exam::service::Service::new(Arc::new(exam_dao)));
    let state = AppState { exam_service };

    let app = app(state);
    let addr = match cfg!(debug_assertions) {
        true => "localhost:8080",
        false => "0.0.0.0:80",
    };
    let listener = TcpListener::bind(addr)
        .await
        .context("Binding listener to address")?;
    axum::serve(listener, app).await?;
    Ok(())
}

pub fn app(state: AppState) -> Router {
    Router::new()
        .fallback(handler_404)
        .nest(
            "/api/v1",
            Router::new()
                .route(
                    "/examens",
                    get(exam::controller::get_exams).post(exam::controller::create_exam),
                )
                .route(
                    "/examens/:id",
                    get(exam::controller::get_exam).delete(exam::controller::delete_exam),
                ),
        )
        .layer(CorsLayer::permissive())
        .with_state(state)
}

async fn handler_404(OriginalUri(uri): OriginalUri) -> impl IntoResponse {
    (
        StatusCode::NOT_FOUND,
        format!("no resource found in {uri:?}"),
    )
}

#[cfg(test)]
mod test {
    use crate::{
        app,
        dao::interface::{Dao, MockDao},
        exam::{api_error::ApiError, dao::ExamDao, dto::ExamDto, model::Exam, service},
        AppState,
    };
    use anyhow::{anyhow, Context, Result};
    use axum::{
        body::Body,
        http::{Request, StatusCode},
        Json,
    };
    use http_body_util::BodyExt;
    use mockall::predicate::eq;
    use sqlx::postgres::PgPoolOptions;
    use std::sync::Arc;
    use testcontainers::{
        core::{IntoContainerPort, WaitFor},
        runners::AsyncRunner,
        GenericImage, ImageExt,
    };
    use tower::ServiceExt;
    use tracing::info;

    #[tokio::test]
    async fn test_exam_controller() -> Result<()> {
        let mut mock_dao: MockDao<Exam> = MockDao::new();
        let exam = Exam::new(1, 1, 1);

        {
            let exam = exam.clone();
            mock_dao
                .expect_find_all()
                .times(1)
                .returning(move || Ok(vec![exam.clone()]));
        }

        let service = service::Service::new(Arc::new(mock_dao));

        let app = app(AppState {
            exam_service: service.into(),
        });

        let response = app
            .oneshot(
                Request::builder()
                    .uri("/api/v1/examens")
                    .body(Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::OK);
        let body = response.into_body().collect().await.unwrap().to_bytes();
        let expected = serde_json::to_string(&vec![exam])?;
        assert_eq!(&body[..], expected.as_bytes());

        Ok(())
    }

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

        let res = dao.insert(&Exam::new(1, 2, 3)).await.context("insert")?;
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
        mock.expect_insert().return_once(|_exam: &Exam| Ok(true));
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
        let (code, _) = service.create_exam(exam_dto).await.unwrap();
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
