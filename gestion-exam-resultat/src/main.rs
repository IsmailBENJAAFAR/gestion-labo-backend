use axum::{routing::get, Router};
use tokio::net::TcpListener;

async fn hello() -> &'static str {
    "This is the microservice for exams and results"
}

#[tokio::main]
async fn main() {
    let app = Router::new().route("/hello", get(hello));

    let listener = TcpListener::bind("127.0.0.1:8080").await.unwrap();
    axum::serve(listener, app).await.unwrap();
}
