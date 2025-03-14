use std::{cell::RefCell, net::SocketAddr, time::Instant};

use axum::{
    extract::{ConnectInfo, Request},
    http::HeaderValue,
    response::Response,
};
use serde_json::json;
use tokio::{task, task_local};
use uuid::Uuid;

use crate::config::get_config;

task_local! {
    pub static LOG_MESSAGE: RefCell<String>;
}

pub struct RequestLogContext {
    pub start: Instant,
    pub request_id: String,
    pub method: String,
    pub url: String,
    pub source_host: String,
    pub status_code: Option<u16>,
    pub log_level: Option<u16>,
    pub log_level_name: Option<String>,
}

impl RequestLogContext {
    pub fn new(req: &Request) -> Self {
        let forwarded_for = req
            .headers()
            .get("X-Forwarded-For")
            .and_then(|hv: &HeaderValue| hv.to_str().ok());
        RequestLogContext {
            start: Instant::now(),
            request_id: req
                .headers()
                .get("X-Request-ID")
                .and_then(|header| header.to_str().ok())
                .map(String::from)
                .unwrap_or_else(|| format!("metadata-service-{}", Uuid::new_v4())),
            method: req.method().to_string(),
            url: req.uri().to_string(),
            source_host: match forwarded_for {
                Some(forwarded_for) => forwarded_for.to_string(),
                None => req
                    .extensions()
                    .get::<ConnectInfo<SocketAddr>>()
                    .map(|ConnectInfo(addr)| addr.ip().to_string())
                    .unwrap_or("Unknown".to_string()),
            }
            .to_string(),
            status_code: None,
            log_level: None,
            log_level_name: None,
        }
    }

    pub fn add_response_fields(self: &mut Self, response: &Response) {
        self.status_code = Some(response.status().as_u16());
        (self.log_level, self.log_level_name) = match response.status().as_u16() {
            200..=299 => (Some(10), Some("INFO".to_string())),
            400..=499 => (Some(20), Some("WARNING".to_string())),
            _ => (Some(30), Some("ERROR".to_string())),
        };
    }
}

pub async fn log_request(log_message: String, request_log_context: &RequestLogContext) {
    let log_entry = json!({
        "@timestamp": chrono::Utc::now().to_rfc3339_opts(chrono::SecondsFormat::Millis, true),
        "command": get_config().command,
        "error.stack": "",
        "host": get_config().docker_host_name,
        "message": log_message,
        "method": request_log_context.method,
        "url": request_log_context.url,
        "statusCode": request_log_context.status_code,
        "responseTime": request_log_context.start.elapsed().as_millis(),
        "serviceName": "metadata-service",
        "serviceVersion": get_config().commit_id,
        "schemaVersion": "v3",
        "xRequestId": request_log_context.request_id,
        "source_host": request_log_context.source_host,
        "thread": task::id().to_string(),
        "level": request_log_context.log_level,
        "levelName": request_log_context.log_level_name,
        "loggerName": "request_logger",
    });
    println!("{log_entry}");
}

#[macro_export]
macro_rules! log {
    ($msg:expr) => {
        use crate::logging::LOG_MESSAGE;
        LOG_MESSAGE.with(|s| *s.borrow_mut() = $msg);
    };
}

#[macro_export]
macro_rules! run_handler_with_log_context {
    ($req:expr, $next:expr) => {{
        use crate::logging::LOG_MESSAGE;
        use std::cell::RefCell;
        LOG_MESSAGE
            .scope(RefCell::new("Responded".to_string()), async move {
                let response = $next.run($req).await;
                let log_message = LOG_MESSAGE.get().borrow().to_string();
                (response, log_message)
            })
            .await
    }};
}
