use std::collections::HashMap;

use axum::Json;
use serde_json::Value;

use crate::{
    errors::ApiError,
    fs::{
        self,
        models::{DataStructure, DatastoreVersions, MetadataAll},
    },
    models::Version,
};

pub async fn find_all_datastore_versions() -> Result<Json<DatastoreVersions>, ApiError> {
    let draft_version = crate::fs::get_draft_version().await?;
    let mut datastore_versions = crate::fs::get_datastore_versions().await?;
    datastore_versions.add_draft_version(draft_version);
    Ok(Json(datastore_versions))
}

pub async fn find_current_data_structure_status(
    status_query_names: Vec<String>,
) -> Result<Json<HashMap<String, Value>>, ApiError> {
    let datastore_versions = find_all_datastore_versions().await?;
    let data_structure_statuses = datastore_versions
        .0
        .get_latest_status_for_data_structures(status_query_names)
        .map_err(|e| {
            ApiError::InternalServerError(format!("Could not get data structure statuses: {e}"))
        })?;
    Ok(Json(data_structure_statuses))
}

pub async fn find_data_structures(
    names: Vec<String>,
    version: Version,
    include_attributes: bool,
    skip_code_lists: bool,
) -> Result<Json<Vec<DataStructure>>, ApiError> {
    fs::validate_version(&version).await?;
    let metadata_all = &fs::get_metadata_all(version).await?;
    let mut data_structures = metadata_all.get_data_structures(names);

    for data_structure in &mut data_structures {
        if !include_attributes {
            data_structure.remove_attributes();
        }
        if skip_code_lists {
            data_structure.clear_code_list_and_missing_values();
        }
    }
    Ok(Json(data_structures))
}

pub async fn find_all_metadata(
    version: Version,
    skip_code_lists: bool,
) -> Result<Json<MetadataAll>, ApiError> {
    fs::validate_version(&version).await?;
    let mut metadata_all = crate::fs::get_metadata_all(version).await?;
    if skip_code_lists {
        metadata_all.clear_code_list_and_missing_values();
    }
    Ok(Json(metadata_all))
}

pub async fn find_all_data_structures_ever() -> Result<Json<Value>, ApiError> {
    let datastore_versions = find_all_datastore_versions().await?;
    let unique_names = datastore_versions
        .0
        .get_all_unique_names()
        .map_err(|e| ApiError::InternalServerError(format!("Invalid datastore versions: {e}")))?;
    Ok(Json(unique_names.into()))
}

pub fn find_languages() -> Json<Value> {
    Json(serde_json::json!([{
        "code": "no",
        "label": "Norsk",
    }]))
}
