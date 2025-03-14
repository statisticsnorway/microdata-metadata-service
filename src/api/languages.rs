use axum::{response::IntoResponse, routing::*, Router};

use crate::core;

pub async fn routes() -> Router {
    Router::new().route("/", get(get_languages))
}

async fn get_languages() -> impl IntoResponse {
    core::find_languages()
}
