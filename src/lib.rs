pub mod api;
pub mod config;
pub mod core;
pub mod errors;
pub mod fs;
pub mod logging;
pub mod models;

pub async fn app() -> axum::Router {
    config::init_config();
    api::get_routes().await
}
