use std::net::SocketAddr;

use metadata_service::config::get_config;
use tokio;

#[tokio::main(flavor = "multi_thread", worker_threads = 1)]
async fn main() {
    let app = metadata_service::app()
        .await
        .into_make_service_with_connect_info::<SocketAddr>();
    let port = get_config().port.clone();
    let listener = tokio::net::TcpListener::bind(format!("0.0.0.0:{port}"))
        .await
        .unwrap();
    println!("Listening on {}", listener.local_addr().unwrap());
    axum::serve(listener, app).await.unwrap();
}
