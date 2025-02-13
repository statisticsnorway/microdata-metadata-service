use axum::http::StatusCode;

#[macro_use]
mod common;
use common::expected;
use common::test_utils::{create_request, setup_test_server};
use serde_json::json;

#[tokio::test]
async fn test_datastore_info() {
    api_test! {
        GET "/metadata/data-store"
        should answer with StatusCode::OK, and expected::datastore_versions()
    }
}

#[tokio::test]
async fn test_metadata_all() {
    api_test! {
        GET "/metadata/all?version=1.0.0.0"
        should answer with StatusCode::OK, and expected::metadata_all()
    }
}

#[tokio::test]
async fn test_data_structures() {
    api_test! {
        GET "/metadata/data-structures?names=TEST_PERSON_INCOME&version=1.0.0.0"
        should answer with StatusCode::OK, and json!([expected::data_structure_test_person_income()])
    }
    api_test! {
        GET "/metadata/data-structures?names=TEST_PERSON_INCOME,TEST_PERSON_PETS&version=1.0.0.0"
        should answer with StatusCode::OK,
        and json!([
            expected::data_structure_test_person_income(),
            expected::data_structure_test_person_pets()
        ])
    }
    api_test! {
        GET "/metadata/data-structures?version=1.0.0.0"
        should answer with StatusCode::OK,
        and json!([
            expected::data_structure_test_person_income(),
            expected::data_structure_test_person_pets()
        ])
    }
}

#[tokio::test]
async fn test_get_data_structures_status() {
    api_test! {
        GET "/metadata/data-structures/status?names=TEST_PERSON_INCOME",
        should answer with StatusCode::OK, and expected::test_person_income_data_structure_status()
    }
    api_test! {
        GET "/metadata/data-structures/status",
        should answer with StatusCode::OK, and expected::all_data_structures_status()
    }
}

#[tokio::test]
async fn test_post_data_structures_status() {
    api_test! {
        POST "/metadata/data-structures/status"
        with json!({"names": "TEST_PERSON_INCOME"}),
        should answer with StatusCode::OK, and expected::test_person_income_data_structure_status()
    }
    api_test! {
        POST "/metadata/data-structures/status" with json!({"names": ""}),
        should answer with StatusCode::OK, and expected::all_data_structures_status()
    }
}

#[tokio::test]
async fn test_languages() {
    api_test! {
        GET "/languages"
        should answer with StatusCode::OK, and expected::languages()
    }
}

#[tokio::test]
async fn test_not_found() {
    api_test! {
        GET "/bad/path"
        should answer with StatusCode::NOT_FOUND
    }
}

#[tokio::test]
async fn test_bad_request() {
    api_test! {
        GET "/metadata/data-structures"
        should answer with StatusCode::BAD_REQUEST,
        and "Failed to deserialize query string: missing field `version`"
    }
    api_test! {
        GET "/metadata/data-structures?version=1.2.3"
        should answer with StatusCode::BAD_REQUEST,
        and "Failed to deserialize query string: Invalid SEMANTIC version format: 1.2.3"
    }
}
