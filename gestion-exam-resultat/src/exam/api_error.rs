use axum::{http::StatusCode, response::IntoResponse};

pub struct ApiError {
    error: anyhow::Error,
    status: Option<StatusCode>,
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

impl IntoResponse for ApiError {
    fn into_response(self) -> axum::response::Response {
        match self.status {
            Some(status) => (status, format!("Error: {}", self.error)).into_response(),
            None => (
                StatusCode::INTERNAL_SERVER_ERROR,
                format!("Something went wrong: {}", self.error),
            )
                .into_response(),
        }
    }
}
