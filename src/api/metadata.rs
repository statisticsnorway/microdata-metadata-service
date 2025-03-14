use std::collections::HashMap;

use axum::{extract::Query, routing::*, Json, Router};
use serde_json::Value;

use crate::{
    api::models::{DataStructureNamesQuery, MetadataAllQuery, MetadataQuery},
    core,
    errors::ApiError,
    fs::models::{DataStructure, DatastoreVersions, MetadataAll},
};

pub async fn routes() -> Router {
    Router::new()
        .route("/data-store", get(get_datastore))
        .route("/data-structures", get(get_data_structures))
        .route("/data-structures/status", get(get_data_structures_status))
        .route("/data-structures/status", post(post_data_structures_status))
        .route("/all-data-structures", get(get_all_data_structures))
        .route("/all", get(get_metadata_all))
}

async fn get_datastore() -> Result<Json<DatastoreVersions>, ApiError> {
    core::find_all_datastore_versions().await
}

async fn get_data_structures(
    Query(query): Query<MetadataQuery>,
) -> Result<Json<Vec<DataStructure>>, ApiError> {
    core::find_data_structures(
        query.split_names(),
        query.version,
        query.include_attributes.unwrap_or(false),
        query.skip_code_lists.unwrap_or(false),
    )
    .await
}

async fn get_data_structures_status(
    Query(query): Query<DataStructureNamesQuery>,
) -> Result<Json<HashMap<String, Value>>, ApiError> {
    core::find_current_data_structure_status(query.split_names()).await
}

async fn post_data_structures_status(
    Json(query): Json<DataStructureNamesQuery>,
) -> Result<Json<HashMap<String, Value>>, ApiError> {
    core::find_current_data_structure_status(query.split_names()).await
}

async fn get_all_data_structures() -> Result<Json<serde_json::Value>, ApiError> {
    core::find_all_data_structures_ever().await
}

async fn get_metadata_all(
    Query(query): Query<MetadataAllQuery>,
) -> Result<Json<MetadataAll>, ApiError> {
    core::find_all_metadata(query.version, query.skip_code_lists.unwrap_or(false)).await
}
