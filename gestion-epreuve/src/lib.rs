use anyhow::Context;
use axum::{
    extract::{FromRef, OriginalUri},
    http::StatusCode,
    response::IntoResponse,
    routing::get,
    Router,
};
use dotenvy::dotenv;
use epreuve::dao::EpreuveDao;
use message_queue::{QueueInstance, QueueMessage};
use sqlx::{migrate::MigrateDatabase, postgres::PgPoolOptions};
use std::sync::Arc;
use tokio::{
    net::TcpListener,
    sync::mpsc::{Receiver, Sender},
};
use tower_http::cors::CorsLayer;

mod dao;
mod epreuve;
mod message_queue;

#[derive(Clone)]
pub struct AppState {
    pub epreuve_service: Arc<epreuve::service::Service>,
    pub mess_queue_channel: Arc<Sender<QueueMessage>>,
}

impl FromRef<AppState> for Arc<epreuve::service::Service> {
    fn from_ref(input: &AppState) -> Self {
        Arc::clone(&input.epreuve_service)
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

    let epreuve_dao = EpreuveDao::new(pool);
    let epreuve_service = Arc::new(epreuve::service::Service::new(Arc::new(epreuve_dao)));
    let (tx, rx) = tokio::sync::mpsc::channel::<QueueMessage>(1024);
    run_message_queue_handler(rx);
    let state = AppState {
        epreuve_service,
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
        QueueInstance::new().run(rx).await;
    });
}

fn app(state: AppState) -> Router {
    Router::new()
        .fallback(handler_404)
        .nest(
            "/api/v1",
            Router::new()
                .route(
                    "/epreuves",
                    get(epreuve::controller::get_epreuves)
                        .post(epreuve::controller::create_epreuve),
                )
                .route(
                    "/epreuves/:id",
                    get(epreuve::controller::get_epreuve)
                        .patch(epreuve::controller::update_epreuve)
                        .delete(epreuve::controller::delete_epreuve),
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
        epreuve::{
            api_error::ApiError,
            dao::EpreuveDao,
            dto::{CreateEpreuveDto, UpdateEpreuveDto},
            model::Epreuve,
            service,
        },
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
    async fn test_epreuve_controller() -> Result<()> {
        let mut mock_dao: MockDao<Epreuve> = MockDao::new();
        let epreuve = Epreuve::new("math", 1);
        {
            let epreuve = epreuve.clone();
            mock_dao
                .expect_find_all()
                .times(1)
                .returning(move || Ok(vec![epreuve.clone()]));
        }
        mock_dao.expect_insert().return_once(move |_| Ok(1));
        {
            let epreuve = epreuve.clone();
            mock_dao
                .expect_find()
                .with(eq(1))
                .return_once(move |_| Ok(epreuve.clone()));
        }
        mock_dao.expect_remove().return_once(|_| Ok(true));

        let service = service::Service::new(Arc::new(mock_dao));
        let (tx, _) = tokio::sync::mpsc::channel(1);
        let app = app(AppState {
            epreuve_service: service.into(),
            mess_queue_channel: Arc::new(tx),
        });

        let response = app
            .clone()
            .oneshot(
                Request::builder()
                    .uri("/api/v1/epreuves")
                    .body(Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::OK);
        let body = response.into_body().collect().await.unwrap().to_bytes();
        let expected = serde_json::to_string(&vec![epreuve.clone()])?;
        assert_eq!(&body[..], expected.as_bytes());

        let response = app
            .clone()
            .oneshot(
                Request::builder()
                    .uri("/api/v1/epreuves/1")
                    .body(Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::OK);
        let body = response.into_body().collect().await.unwrap().to_bytes();
        let expected = serde_json::to_string(&epreuve)?;
        assert_eq!(&body[..], expected.as_bytes());

        let response = app
            .clone()
            .oneshot(
                Request::builder()
                    .uri("/api/v1/epreuves")
                    .method("POST")
                    .header("Content-Type", "application/json")
                    .body(Body::from(serde_json::to_string(&epreuve.clone())?))
                    .unwrap(),
            )
            .await
            .unwrap();

        let post_epreuve = Epreuve::with_id(1, &epreuve.nom, epreuve.fk_id_analyse);

        // TODO: fix controller tests
        assert_eq!(response.status(), StatusCode::CREATED);
        let body = response.into_body().collect().await.unwrap().to_bytes();
        let body: Value = serde_json::from_slice(&body).unwrap();
        let post_epreuve_value: Value = serde_json::to_value(post_epreuve)?;
        assert_eq!(body.get("dossierId"), post_epreuve_value.get("dossierId"));
        assert_eq!(body.get("epreuveId"), post_epreuve_value.get("epreuveId"));
        assert_eq!(
            body.get("testAnalyseId"),
            post_epreuve_value.get("testAnalyseId")
        );

        let response = app
            .clone()
            .oneshot(
                Request::builder()
                    .uri("/api/v1/epreuves/1")
                    .method("DELETE")
                    .header("Content-Type", "application/json")
                    .body(Body::from(serde_json::to_string(&epreuve.clone())?))
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::NO_CONTENT);

        Ok(())
    }

    #[tokio::test]
    async fn test_epreuve_database() -> Result<()> {
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

        let dao = EpreuveDao::new(pool);

        let res = dao.find(1).await;
        assert!(res.is_err());

        let res = dao
            .insert(&Epreuve::new("math", 3))
            .await
            .context("insert")?;
        assert_eq!(res, 1);

        let res = dao.find(1).await?;
        assert_eq!((res.nom.as_str(), res.fk_id_analyse,), ("math", 3));

        let res = dao.find_all().await?;
        assert_eq!(res.len(), 1);
        let res = &res[0];
        assert_eq!((res.nom.as_str(), res.fk_id_analyse), ("math", 3));

        let mut updated_epreuve = Epreuve::new("info", 6);
        updated_epreuve.id = 1;
        let res = dao.update(&updated_epreuve).await?;
        assert_eq!((res.nom.as_str(), res.fk_id_analyse), ("info", 6));

        let res = dao.find(1).await?;
        assert_eq!((res.nom.as_str(), res.fk_id_analyse), ("info", 6));

        let res = dao.remove(1).await?;
        assert!(res);

        let res = dao.find_all().await?;
        assert_eq!(res.len(), 0);

        let res = dao.find(1).await;
        assert!(res.is_err());

        Ok(())
    }

    #[tokio::test]
    async fn test_epreuve_service() {
        let mut mock: MockDao<Epreuve> = MockDao::new();
        let epreuve = Epreuve::new("math", 1);

        // Mocking the DAO
        mock.expect_find_all().times(1).returning(|| Ok(vec![]));
        mock.expect_insert().return_once(|_epreuve: &Epreuve| Ok(1));
        {
            let epreuve = epreuve.clone();
            mock.expect_find_all()
                .times(1)
                .returning(move || Ok(vec![epreuve.clone()]));
        }
        {
            let epreuve = epreuve.clone();
            mock.expect_find()
                .with(eq(1))
                .return_once(move |_f| Ok(epreuve.clone()));
        }
        {
            let mut epreuve = epreuve.clone();
            epreuve.id = 1;
            epreuve.nom = "info".to_string();
            epreuve.fk_id_analyse = 6;
            mock.expect_update()
                .withf(|epreuve: &Epreuve| {
                    (epreuve.id, epreuve.nom.as_str(), epreuve.fk_id_analyse) == (1, "info", 6)
                })
                .return_once(move |_epreuve: &Epreuve| Ok(epreuve.clone()));
        }
        mock.expect_find()
            .with(eq(2))
            .return_once(|_f| Err(anyhow!("error")));

        let service = service::Service::new(Arc::new(mock));
        // Using the epreuves service with the MockDao object
        let (code, Json(data)) = service.get_epreuves().await.unwrap();
        assert_eq!((code, data.len()), (StatusCode::OK, 0));

        let epreuve_create_dto = CreateEpreuveDto::new("math", 2);
        let (code, _) = service.create_epreuve(epreuve_create_dto).await.unwrap();
        assert_eq!(code, StatusCode::CREATED);

        let epreuve_update_dto = UpdateEpreuveDto::new("info", 6);
        let (code, Json(updated_epreuve)) =
            service.update_epreuve(1, epreuve_update_dto).await.unwrap();
        assert_eq!(code, StatusCode::OK);
        assert_eq!(
            (updated_epreuve.nom.as_str(), updated_epreuve.fk_id_analyse),
            ("info", 6)
        );

        let (code, Json(data)) = service.get_epreuves().await.unwrap();
        assert_eq!(
            (code, serde_json::to_string(&data).unwrap()),
            (StatusCode::OK, serde_json::to_string(&[epreuve]).unwrap())
        );

        if let Err(ApiError {
            status: Some(status),
            ..
        }) = service.get_epreuve(2).await
        {
            assert_eq!(StatusCode::NOT_FOUND, status);
        } else {
            panic!("service.get_epreuve failed");
        }
    }
}
