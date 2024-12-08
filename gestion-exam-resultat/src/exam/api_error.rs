use axum::{http::StatusCode, response::IntoResponse, Json};
use serde::Serialize;

#[derive(Debug)]
pub struct ApiError {
    error: anyhow::Error,
    pub status: Option<StatusCode>,
}

impl ApiError {
    pub fn new(error: anyhow::Error, status: Option<StatusCode>) -> ApiError {
        ApiError { error, status }
    }
}

impl<E> From<E> for ApiError
where
    E: Into<anyhow::Error>,
{
    fn from(err: E) -> Self {
        Self {
            error: err.into(),
            status: None,
        }
    }
}

#[derive(Serialize)]
struct SerError {
    message: String,
}

impl IntoResponse for ApiError {
    fn into_response(self) -> axum::response::Response {
        let err = SerError {
            message: format!("error: {}", self.error),
        };
        match self.status {
            Some(status) => (status, Json(err)).into_response(),
            None => (StatusCode::INTERNAL_SERVER_ERROR, Json(err)).into_response(),
        }
    }
}
