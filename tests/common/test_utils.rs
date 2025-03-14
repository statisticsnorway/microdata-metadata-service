use reqwest::{Client, RequestBuilder};
use tokio::sync::oneshot;

pub struct TestServer {
    pub base_url: String,
    pub shutdown_sender: oneshot::Sender<()>,
}

pub async fn setup_test_server() -> TestServer {
    let app = metadata_service::app().await;
    let listener = tokio::net::TcpListener::bind("127.0.0.1:0").await.unwrap();
    let addr = listener.local_addr().unwrap();
    let (shutdown_sender, shutdown_receiver) = oneshot::channel::<()>();
    tokio::spawn(async move {
        axum::serve(listener, app)
            .with_graceful_shutdown(async { shutdown_receiver.await.unwrap() })
            .await
            .unwrap()
    });
    TestServer {
        base_url: format!("http://{}", addr),
        shutdown_sender,
    }
}

pub async fn create_request(method: &str, url: &str) -> RequestBuilder {
    let request = match method {
        "POST" => Client::new().post(url),
        "PUT" => Client::new().put(url),
        "DELETE" => Client::new().delete(url),
        "GET" => Client::new().get(url),
        _ => panic!("Unsupported HTTP method"),
    };
    request
        .header("X-Request-ID", "test-123")
        .header("Accept-Language", "no")
        .header("Accept", "application/json")
}

#[macro_export]
macro_rules! api_test {
    (
        $method:ident $url:literal with $req_body:expr, should answer with $status:expr, and $expected_response:expr
    ) => {
        let server = setup_test_server().await;
        let url = format!("{}{}", server.base_url, $url);
        let request = create_request(stringify!($method), &url).await;

        let response = request.json(&$req_body).send().await.unwrap();

        assert_eq!(response.status(), $status);

        let response_text = response.text().await.unwrap();
        if let Ok(json_response) = serde_json::from_str::<serde_json::Value>(&response_text) {
            assert_eq!(json_response, $expected_response);
        } else {
            let string_response: String = response_text;
            assert_eq!(string_response, $expected_response);
        }
        let _ = server.shutdown_sender.send(());
    };
    (
        $method:ident $url:literal, should answer with $status:expr, and $expected_response:expr
    ) => {
        let server = setup_test_server().await;
        let url = format!("{}{}", server.base_url, $url);
        let request = create_request(stringify!($method), &url).await;

        let response = request.send().await.unwrap();

        assert_eq!(response.status(), $status);

        let response_text = response.text().await.unwrap();
        if let Ok(json_response) = serde_json::from_str::<serde_json::Value>(&response_text) {
            assert_eq!(json_response, $expected_response);
        } else {
            let string_response: String = response_text;
            assert_eq!(string_response, $expected_response);
        }
        let _ = server.shutdown_sender.send(());
    };
    (
        $method:ident $url:literal with $req_body:expr, should answer with $status:expr
    ) => {
        let server = setup_test_server().await;
        setup_test_db().await;
        let url = format!("{}{}", server.base_url, $url);
        let request = create_request(stringify!($method), &url).await;

        let response = request.json(&$req_body).send().await.unwrap();

        assert_eq!(response.status(), $status);
        let _ = server.shutdown_sender.send(());
    };
    (
        $method:ident $url:literal should answer with $status:expr, and $expected_response:expr
    ) => {
        let server = setup_test_server().await;
        let url = format!("{}{}", server.base_url, $url);
        let response = create_request(stringify!($method), &url)
            .await
            .send()
            .await
            .unwrap();

        assert_eq!(response.status(), $status);
        let response_text = response.text().await.unwrap();
        if let Ok(json_response) = serde_json::from_str::<serde_json::Value>(&response_text) {
            assert_eq!(json_response, $expected_response);
        } else {
            let string_response: String = response_text;
            assert_eq!(string_response, $expected_response);
        }
        let _ = server.shutdown_sender.send(());
    };
    (
        $method:ident $url:literal should answer with $status:expr
    ) => {
        let server = setup_test_server().await;
        let url = format!("{}{}", server.base_url, $url);
        let response = create_request(stringify!($method), &url)
            .await
            .send()
            .await
            .unwrap();

        assert_eq!(response.status(), $status);
        let _ = server.shutdown_sender.send(());
    };
}
