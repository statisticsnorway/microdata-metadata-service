use crate::models::Version;

#[derive(Debug, serde::Deserialize)]
pub struct MetadataQuery {
    pub names: Option<String>,
    pub version: Version,
    pub include_attributes: Option<bool>,
    pub skip_code_lists: Option<bool>,
}

#[derive(Debug, serde::Deserialize)]
pub struct MetadataAllQuery {
    pub version: Version,
    pub skip_code_lists: Option<bool>,
}

#[derive(Debug, serde::Deserialize)]
pub struct DataStructureNamesQuery {
    pub names: Option<String>,
}

fn split_names(names: &Option<String>) -> Vec<String> {
    names
        .clone()
        .unwrap_or("".to_string())
        .split(',')
        .filter(|s| !s.is_empty())
        .map(|s| {
            println!("{s}");
            s.trim().to_string()
        })
        .collect()
}

impl MetadataQuery {
    pub fn split_names(&self) -> Vec<String> {
        split_names(&self.names)
    }
}

impl DataStructureNamesQuery {
    pub fn split_names(&self) -> Vec<String> {
        split_names(&self.names)
    }
}
