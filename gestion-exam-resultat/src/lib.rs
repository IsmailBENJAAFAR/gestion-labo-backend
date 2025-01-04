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
use message_queue::{QueueInstance, QueueMessage};
use resultat::dao::ResultatDao;
use sqlx::{migrate::MigrateDatabase, postgres::PgPoolOptions};
use std::sync::Arc;
use tokio::{
    net::TcpListener,
    sync::mpsc::{Receiver, Sender},
};
use tower_http::cors::CorsLayer;

mod api_error;
mod dao;
mod exam;
mod message_queue;
mod resultat;

// TODO: Add another resource for resultat, and exam should contain a list of resultats whenever
// you fetch exams
// TODO: Add dashboard info for exam
// TODO: Add seed for database

#[derive(Clone)]
pub struct AppState {
    pub exam_service: Arc<exam::service::Service>,
    pub resultat_service: Arc<resultat::service::Service>,
    pub mess_queue_channel: Arc<Sender<QueueMessage>>,
}

impl FromRef<AppState> for Arc<exam::service::Service> {
    fn from_ref(input: &AppState) -> Self {
        Arc::clone(&input.exam_service)
    }
}

impl FromRef<AppState> for Arc<resultat::service::Service> {
    fn from_ref(input: &AppState) -> Self {
        Arc::clone(&input.resultat_service)
    }
}

impl FromRef<AppState> for Arc<Sender<QueueMessage>> {
    fn from_ref(input: &AppState) -> Self {
        Arc::clone(&input.mess_queue_channel)
    }
}

pub async fn run_app() -> anyhow::Result<()> {
    tracing_subscriber::fmt().init();

    match dotenv() {
        Ok(f) => tracing::info!(".env file: {f:?} loaded successfully."),
        Err(_) => tracing::error!("Warning: .env file not found."),
    };
    let url = std::env::var("DATABASE_URL").context("Please set DATABASE_URL for database")?;

    if !sqlx::Postgres::database_exists(&url).await? {
        if let Err(e) = sqlx::Postgres::create_database(&url).await {
            tracing::error!("error creating database: {e}");
        }
    }

    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&url)
        .await
        .context("can't connect to database")?;

    if let Err(e) = sqlx::migrate!("./migrations/").run(&pool).await {
        tracing::error!("error migrating: {e}");
    }

    let exam_dao = ExamDao::new(pool.clone());
    let resultat_dao = ResultatDao::new(pool);
    let exam_service = Arc::new(exam::service::Service::new(Arc::new(exam_dao)));
    let resultat_service = Arc::new(resultat::service::Service::new(Arc::new(resultat_dao)));
    let (tx, rx) = tokio::sync::mpsc::channel::<QueueMessage>(1024);
    run_message_queue_handler(rx);
    let state = AppState {
        exam_service,
        resultat_service,
        mess_queue_channel: Arc::new(tx),
    };

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

fn run_message_queue_handler(rx: Receiver<QueueMessage>) {
    tokio::spawn(async move {
        match QueueInstance::new().await {
            Ok(instance) => instance.run(rx).await,
            Err(e) => tracing::error!("error starting message queue handler: {e}"),
        };
    });
}

fn app(state: AppState) -> Router {
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
                    get(exam::controller::get_exam)
                        .patch(exam::controller::update_exam)
                        .delete(exam::controller::delete_exam),
                )
                .route(
                    "/resultats",
                    get(resultat::controller::get_resultats)
                        .post(resultat::controller::create_resultat),
                )
                .route(
                    "/resultats/:id",
                    get(resultat::controller::get_resultat)
                        .patch(resultat::controller::update_resultat)
                        .delete(resultat::controller::delete_resultat),
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
        api_error::ApiError,
        app,
        dao::interface::{Dao, MockDao},
        exam::{
            dao::ExamDao,
            dto::{CreateExamDto, UpdateExamDto},
            model::Exam,
            service,
        },
        resultat::{self, model::Resultat},
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
    use serde_json::Value;
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
        let mut mock_exam_dao: MockDao<Exam> = MockDao::new();
        let mock_res_dao: MockDao<Resultat> = MockDao::new();
        let exam = Exam::new(1, 1, 1);
        {
            let exam = exam.clone();
            mock_exam_dao
                .expect_find_all()
                .times(1)
                .returning(move || Ok(vec![exam.clone()]));
        }
        mock_exam_dao.expect_insert().return_once(move |_| Ok(1));
        {
            let exam = exam.clone();
            mock_exam_dao
                .expect_find()
                .with(eq(1))
                .return_once(move |_| Ok(exam.clone()));
        }
        mock_exam_dao.expect_remove().return_once(|_| Ok(true));

        let service_exam = service::Service::new(Arc::new(mock_exam_dao));
        let service_res = resultat::service::Service::new(Arc::new(mock_res_dao));
        let (tx, _) = tokio::sync::mpsc::channel(1);
        let app = app(AppState {
            exam_service: service_exam.into(),
            resultat_service: service_res.into(),
            mess_queue_channel: Arc::new(tx),
        });

        let response = app
            .clone()
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
        let expected = serde_json::to_string(&vec![exam.clone()])?;
        assert_eq!(&body[..], expected.as_bytes());

        let response = app
            .clone()
            .oneshot(
                Request::builder()
                    .uri("/api/v1/examens/1")
                    .body(Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::OK);
        let body = response.into_body().collect().await.unwrap().to_bytes();
        let expected = serde_json::to_string(&exam)?;
        assert_eq!(&body[..], expected.as_bytes());

        let response = app
            .clone()
            .oneshot(
                Request::builder()
                    .uri("/api/v1/examens")
                    .method("POST")
                    .header("Content-Type", "application/json")
                    .body(Body::from(serde_json::to_string(&exam.clone())?))
                    .unwrap(),
            )
            .await
            .unwrap();

        let post_exam = Exam::with_id(
            1,
            exam.fk_num_dossier,
            exam.fk_id_epreuve,
            exam.fk_id_test_analyse,
        );

        assert_eq!(response.status(), StatusCode::CREATED);
        let body = response.into_body().collect().await.unwrap().to_bytes();
        let body: Value = serde_json::from_slice(&body).unwrap();
        let post_exam_value: Value = serde_json::to_value(post_exam)?;
        assert_eq!(body.get("dossierId"), post_exam_value.get("dossierId"));
        assert_eq!(body.get("epreuveId"), post_exam_value.get("epreuveId"));
        assert_eq!(
            body.get("testAnalyseId"),
            post_exam_value.get("testAnalyseId")
        );

        let response = app
            .clone()
            .oneshot(
                Request::builder()
                    .uri("/api/v1/examens/1")
                    .method("DELETE")
                    .header("Content-Type", "application/json")
                    .body(Body::from(serde_json::to_string(&exam.clone())?))
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::NO_CONTENT);

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
        assert_eq!(res, 1);

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

        let mut updated_exam = Exam::new(4, 5, 6);
        updated_exam.id = 1;
        let res = dao.update(&updated_exam).await?;
        assert_eq!(
            (
                res.fk_num_dossier,
                res.fk_id_epreuve,
                res.fk_id_test_analyse
            ),
            (4, 5, 6)
        );

        let res = dao.find(1).await?;
        assert_eq!(
            (
                res.fk_num_dossier,
                res.fk_id_epreuve,
                res.fk_id_test_analyse
            ),
            (4, 5, 6)
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
        mock.expect_insert().return_once(|_exam: &Exam| Ok(1));
        {
            let exam = exam.clone();
            mock.expect_find_all()
                .times(1)
                .returning(move || Ok(vec![exam.clone()]));
        }
        {
            let exam = exam.clone();
            mock.expect_find()
                .with(eq(1))
                .return_once(move |_f| Ok(exam.clone()));
        }
        {
            let mut exam = exam.clone();
            exam.id = 1;
            exam.fk_num_dossier = 4;
            exam.fk_id_epreuve = 5;
            exam.fk_id_test_analyse = 6;
            mock.expect_update()
                .withf(|exam: &Exam| {
                    (
                        exam.id,
                        exam.fk_num_dossier,
                        exam.fk_id_epreuve,
                        exam.fk_id_test_analyse,
                    ) == (1, 4, 5, 6)
                })
                .return_once(move |_exam: &Exam| Ok(exam.clone()));
        }
        mock.expect_find()
            .with(eq(2))
            .return_once(|_f| Err(anyhow!("error")));

        let service = service::Service::new(Arc::new(mock));
        // Using the exams service with the MockDao object
        let (code, Json(data)) = service.get_exams().await.unwrap();
        assert_eq!((code, data.len()), (StatusCode::OK, 0));

        let exam_create_dto = CreateExamDto::new(1, 1, 1);
        let (code, _) = service.create_exam(exam_create_dto).await.unwrap();
        assert_eq!(code, StatusCode::CREATED);

        let exam_update_dto = UpdateExamDto::new(4, 5, 6);
        let (code, Json(updated_exam)) = service.update_exam(1, exam_update_dto).await.unwrap();
        assert_eq!(code, StatusCode::OK);
        assert_eq!(
            (
                updated_exam.fk_num_dossier,
                updated_exam.fk_id_epreuve,
                updated_exam.fk_id_test_analyse
            ),
            (4, 5, 6)
        );

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
