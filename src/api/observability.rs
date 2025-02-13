use axum::{response::IntoResponse, routing::get, Router};

pub async fn routes() -> Router {
    Router::new()
        .route("/alive", get(liveness))
        .route("/ready", get(readiness))
}

async fn liveness() -> impl IntoResponse {
    "Alive"
}

async fn readiness() -> impl IntoResponse {
    "Ready"
}
