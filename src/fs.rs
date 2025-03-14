pub mod models;

use crate::fs::models::{DatastoreVersions, MetadataAll};
use crate::{errors::ApiError, models::Version};
use serde_json::Value;

pub async fn get_draft_version() -> Result<Value, ApiError> {
    let datastore_root_dir = crate::config::get_config().datastore_root_dir.clone();
    let file_path = format!("{datastore_root_dir}/datastore/draft_version.json");
    let file = tokio::fs::read_to_string(&file_path).await.map_err(|e| {
        ApiError::InternalServerError(format!(
            "No such file path for draft version '{file_path}': {e}"
        ))
    })?;
    serde_json::from_str(&file)
        .map_err(|e| ApiError::InternalServerError(format!("'{file_path}' is not valid json: {e}")))
}

pub async fn get_datastore_versions() -> Result<DatastoreVersions, ApiError> {
    let datastore_root_dir = crate::config::get_config().datastore_root_dir.clone();
    let file_path = format!("{datastore_root_dir}/datastore/datastore_versions.json");
    let file = tokio::fs::read_to_string(&file_path).await.map_err(|e| {
        ApiError::InternalServerError(format!(
            "No such file path for datastore versions '{file_path}': {e}"
        ))
    })?;
    let value = serde_json::from_str(&file).map_err(|e| {
        ApiError::InternalServerError(format!("'{file_path}' is not valid json: {e}"))
    })?;
    Ok(DatastoreVersions(value))
}

pub async fn get_metadata_all(version: Version) -> Result<MetadataAll, ApiError> {
    let datastore_root_dir = crate::config::get_config().datastore_root_dir.clone();
    let file_version = version.as_file_version();
    let file_path = format!("{datastore_root_dir}/datastore/metadata_all__{file_version}.json");
    let file = tokio::fs::read_to_string(&file_path).await.map_err(|e| {
        ApiError::NotFoundError(format!(
            "No such file path for metadata all '{file_path}': {e}"
        ))
    })?;
    serde_json::from_str(&file)
        .map_err(|e| ApiError::InternalServerError(format!("'{file_path}' is not valid json: {e}")))
}

pub async fn validate_version(version: &Version) -> Result<(), ApiError> {
    match version {
        Version::DRAFT { timestamp } => {
            if timestamp == &0 {
                return Ok(());
            }
            let draft_version = crate::fs::get_draft_version().await?;
            let current_version_str = draft_version
                .get("version")
                .and_then(|v| v.as_str())
                .ok_or(ApiError::InternalServerError(
                    "Invalid datastore versions: Missing 'version'".to_string(),
                ))?;
            let current_version: Version = Version::from_str(current_version_str).map_err(|e| {
                ApiError::InternalServerError(format!("Invalid draft version in datastore: {e}"))
            })?;
            if *version != current_version {
                Err(ApiError::NotFoundError("No such draft version".to_string()))
            } else {
                Ok(())
            }
        }
        Version::SEMANTIC { .. } => Ok(()),
    }
}
