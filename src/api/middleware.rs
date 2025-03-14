use axum::{
    body::{to_bytes, Body},
    extract::Request,
    http::HeaderValue,
    middleware::Next,
    response::Response,
};

use crate::{
    logging::{log_request, RequestLogContext},
    run_handler_with_log_context,
};

async fn get_log_message_from_response(
    response: Response,
    mut log_message: String,
) -> (Response, String) {
    let (head, body) = response.into_parts();
    let body_bytes = to_bytes(body, usize::MAX)
        .await
        .unwrap_or(axum::body::Bytes::new());
    if let Ok(body_text) = std::str::from_utf8(&body_bytes) {
        log_message = body_text.to_string();
    }
    (
        Response::from_parts(head, Body::from(body_bytes.clone())),
        log_message,
    )
}

async fn response_with_headers(mut response: Response, request_id: String) -> Response {
    response
        .headers_mut()
        .insert("X-Request-ID", HeaderValue::from_str(&request_id).unwrap());
    response
        .headers_mut()
        .insert("Content-Language", HeaderValue::from_static("no"));
    response
}

pub async fn handle_request_context(req: Request, next: Next) -> Response {
    let mut request_log_context = RequestLogContext::new(&req);
    let (mut response, mut log_message) = run_handler_with_log_context!(req, next);
    request_log_context.add_response_fields(&response);
    if request_log_context.log_level == Some(20) {
        (response, log_message) = get_log_message_from_response(response, log_message).await;
    }
    log_request(log_message, &request_log_context).await;
    response_with_headers(response, request_log_context.request_id).await
}
