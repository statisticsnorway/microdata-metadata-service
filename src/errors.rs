use axum::{
    http::StatusCode,
    response::{IntoResponse, Response},
    Json,
};
use serde_json::json;

use crate::log;

pub enum ApiError {
    NotFoundError(String),
    BadRequestError(String),
    InternalServerError(String),
}

impl IntoResponse for ApiError {
    fn into_response(self) -> Response {
        match &self {
            ApiError::NotFoundError(message) => {
                log!(message.to_string());
                (
                    StatusCode::NOT_FOUND,
                    Json(json!({"service": "metadata-service", "message": "Not Found"})),
                )
            },
            ApiError::InternalServerError(message) => {
                log!(message.to_string());
                (
                    StatusCode::INTERNAL_SERVER_ERROR,
                    Json(json!({"service": "metadata-service", "message": "Internal Server Error"})),
                )
            },
            ApiError::BadRequestError(message) => (
                StatusCode::BAD_REQUEST,
                Json(json!({"service": "metadata-service", "message": format!("Bad Request: {message}")})),
            ),
        }
        .into_response()
    }
}

impl std::fmt::Display for ApiError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match &self {
            ApiError::NotFoundError(message) => write!(f, "{message}"),
            ApiError::BadRequestError(message) => write!(f, "{message}"),
            ApiError::InternalServerError(message) => write!(f, "{message}"),
        }
    }
}
