use axum::Router;
use tower_http::compression::CompressionLayer;

mod languages;
mod metadata;
mod middleware;
pub mod models;
mod observability;

pub async fn get_routes() -> Router {
    Router::new()
        .nest("/metadata", metadata::routes().await)
        .nest("/languages", languages::routes().await)
        .nest("/health", observability::routes().await)
        .layer(axum::middleware::from_fn(
            middleware::handle_request_context,
        ))
        .layer(CompressionLayer::new())
}
