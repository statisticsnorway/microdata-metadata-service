use std::collections::{HashMap, HashSet};

use serde::{Deserialize, Serialize};
use serde_json::{json, Value};

#[derive(Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct MetadataAll {
    pub data_store: Value,
    pub data_structures: Vec<DataStructure>,
}

impl MetadataAll {
    pub fn get_data_structures(self: &Self, names: Vec<String>) -> Vec<DataStructure> {
        let filtered_datastructures = if !names.is_empty() {
            self.data_structures
                .iter()
                .filter(|ds| {
                    ds.0.get("name")
                        .and_then(serde_json::Value::as_str)
                        .map(|name| names.contains(&name.to_string()))
                        .unwrap_or(false)
                })
                .cloned()
                .collect()
        } else {
            self.data_structures.clone()
        };
        filtered_datastructures
    }

    pub fn clear_code_list_and_missing_values(self: &mut Self) {
        for ds in self.data_structures.iter_mut() {
            ds.clear_code_list_and_missing_values();
        }
    }
}

#[derive(Serialize, Deserialize, Clone)]
pub struct DataStructure(Value);

impl DataStructure {
    pub fn remove_attributes(self: &mut Self) {
        if let Some(ds) = self.0.as_object_mut() {
            ds.remove("attributeVariables");
        }
    }

    pub fn clear_code_list_and_missing_values(self: &mut Self) {
        fn recursive_clear(value: &mut Value) {
            match value {
                Value::Object(map) => {
                    for (key, val) in map.iter_mut() {
                        if key == "codeList" || key == "missingValues" {
                            *val = Value::Null;
                        } else {
                            recursive_clear(val);
                        }
                    }
                }
                Value::Array(array) => {
                    for item in array.iter_mut() {
                        recursive_clear(item);
                    }
                }
                _ => {}
            }
        }
        recursive_clear(&mut self.0);
    }
}

#[derive(Serialize, Deserialize)]
pub struct DatastoreVersions(pub Value);

impl DatastoreVersions {
    pub fn add_draft_version(self: &mut Self, draft_version: Value) {
        if let Some(versions) = self.0.get_mut("versions").and_then(Value::as_array_mut) {
            versions.insert(0, draft_version);
        } else {
            self.0["versions"] = json!([draft_version])
        }
    }
    pub fn get_all_unique_names(self: &DatastoreVersions) -> Result<Vec<&str>, String> {
        let versions = self
            .0
            .get("versions")
            .and_then(|versions| versions.as_array())
            .ok_or("Invalid datastore versions: Missing 'versions'".to_string())?;
        let all_datastructure_updates = versions
            .iter()
            .map(|version| {
                version
                    .get("dataStructureUpdates")
                    .and_then(|data_structure_updates| data_structure_updates.as_array())
                    .ok_or_else(|| {
                        "Invalid datastore versions: Missing or invalid 'dataStructureUpdates'"
                            .to_string()
                    })
            })
            .collect::<Result<Vec<&Vec<Value>>, String>>()
            .map_err(|e| format!("Invalid datastore versions: {e}"))?;
        Ok(all_datastructure_updates
            .into_iter()
            .flat_map(|data_structure_updates| {
                data_structure_updates
                    .into_iter()
                    .map(|data_structure_update| {
                        data_structure_update
                            .get("name")
                            .and_then(|name| name.as_str())
                            .ok_or_else(|| "Invalid datastore versions: Missing 'name'".to_string())
                    })
            })
            .collect::<Result<HashSet<&str>, String>>()
            .map_err(|e| format!("Invalid datastore versions: {e}"))?
            .into_iter()
            .collect::<Vec<&str>>())
    }

    pub fn get_latest_status_for_data_structures(
        self: &Self,
        status_query_names: Vec<String>,
    ) -> Result<HashMap<String, Value>, String> {
        fn missing_or_invalid(key: &str) -> String {
            format!("Invalid datastore versions: Missing or invalid '{key}'")
        }
        let should_filter = !status_query_names.is_empty();
        let mut data_structure_statuses: HashMap<String, Value> = HashMap::new();
        let versions = self
            .0
            .get("versions")
            .and_then(|v| v.as_array())
            .ok_or(missing_or_invalid("versions"))?;
        for version in versions {
            let data_structure_updates = version
                .get("dataStructureUpdates")
                .and_then(|ds| ds.as_array())
                .ok_or(missing_or_invalid("versions->dataStructureUpdates"))?;
            for data_structure in data_structure_updates {
                let name = data_structure
                    .get("name")
                    .and_then(|n| n.as_str())
                    .ok_or(missing_or_invalid("versions->dataStructureUpdates->name"))?;
                let filter_out = should_filter && !status_query_names.contains(&name.to_string());
                if data_structure_statuses.contains_key(name) || filter_out {
                    continue;
                }
                let operation = data_structure.get("operation").ok_or(missing_or_invalid(
                    "versions->dataStructureUpdates->operation",
                ))?;
                let release_time = version.get("releaseTime").cloned().unwrap_or(json!(null));
                let release_status =
                    data_structure
                        .get("releaseStatus")
                        .ok_or(missing_or_invalid(
                            "versions->dataStructureUpdates->releaseStatus",
                        ))?;
                data_structure_statuses.insert(
                    name.to_string(),
                    json!({
                        "operation": operation,
                        "releaseTime": release_time,
                        "releaseStatus": release_status,
                    }),
                );
            }
        }
        Ok(data_structure_statuses)
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use serde_json::json;

    #[test]
    fn test_metadata_all_get_data_structures() {
        let ds1 = DataStructure(json!({"name": "ds1"}));
        let ds2 = DataStructure(json!({"name": "ds2"}));
        let metadata = MetadataAll {
            data_store: json!({}),
            data_structures: vec![ds1.clone(), ds2.clone()],
        };

        let result = metadata.get_data_structures(vec!["ds1".to_string()]);
        assert_eq!(result.len(), 1);
        assert_eq!(result[0].0, ds1.0);

        let result_all = metadata.get_data_structures(vec![]);
        assert_eq!(result_all.len(), 2);
    }
    #[test]
    fn test_metadata_all_clear_code_list_and_missing_values() {
        let mut metadata = MetadataAll {
            data_store: json!({}),
            data_structures: vec![DataStructure(json!({
                "codeList": "some_value",
                "missingValues": "other_value",
            }))],
        };
        metadata.clear_code_list_and_missing_values();
        assert_eq!(
            metadata.data_structures[0].0.get("codeList"),
            Some(&json!(null))
        );
    }

    #[test]
    fn test_data_structure_clear_code_list_and_missing_values() {
        let mut ds = DataStructure(json!({
            "codeList": "some_value",
            "missingValues": "other_value",
            "nested": { "codeList": "should_clear" }
        }));

        ds.clear_code_list_and_missing_values();

        assert_eq!(ds.0.get("codeList"), Some(&json!(null)));
        assert_eq!(ds.0.get("missingValues"), Some(&json!(null)));
        assert_eq!(ds.0["nested"].get("codeList"), Some(&json!(null)));
    }

    #[test]
    fn test_add_draft_version() {
        let mut datastore = DatastoreVersions(json!({ "versions": [] }));
        let draft_version = json!({ "name": "draft version" });

        datastore.add_draft_version(draft_version.clone());
        assert_eq!(datastore.0["versions"][0], draft_version);
    }

    #[test]
    fn test_get_all_unique_names() {
        let datastore = DatastoreVersions(json!({
            "versions": [
                { "dataStructureUpdates": [{ "name": "ds1" }, { "name": "ds2" }] },
                { "dataStructureUpdates": [{ "name": "ds1" }, { "name": "ds3" }] }
            ]
        }));

        let names = datastore.get_all_unique_names().unwrap();
        let expected: Vec<&str> = vec!["ds1", "ds2", "ds3"];
        assert_eq!(names.len(), 3);
        assert!(expected.iter().all(|&n| names.contains(&n)));
    }

    #[test]
    fn test_get_latest_status_for_data_structures() {
        let datastore = DatastoreVersions(json!({
            "versions": [
                {
                    "releaseTime": "333333333",
                    "dataStructureUpdates": [
                        { "name": "ds1", "operation": "CHANGE", "releaseStatus": "DRAFT" }
                    ]
                },
                {
                    "releaseTime": "222222222",
                    "dataStructureUpdates": [
                        { "name": "ds2", "operation": "REMOVE", "releaseStatus": "REMOVED" }
                    ]
                },
                {
                    "releaseTime": "111111111",
                    "dataStructureUpdates": [
                        { "name": "ds3", "operation": "ADD", "releaseStatus": "RELEASED" },
                        { "name": "ds1", "operation": "ADD", "releaseStatus": "RELEASED" },
                        { "name": "ds2", "operation": "ADD", "releaseStatus": "RELEASED" }
                    ]
                }
            ]
        }));

        let statuses = datastore
            .get_latest_status_for_data_structures(vec![])
            .unwrap();
        assert_eq!(
            statuses["ds1"],
            json!({
                "operation": "CHANGE",
                "releaseTime": "333333333",
                "releaseStatus": "DRAFT"
            })
        );
        assert_eq!(
            statuses["ds2"],
            json!({
                "operation": "REMOVE",
                "releaseTime": "222222222",
                "releaseStatus": "REMOVED"
            })
        );
        assert_eq!(
            statuses["ds3"],
            json!({
                "operation": "ADD",
                "releaseTime": "111111111",
                "releaseStatus": "RELEASED"
            })
        );
    }
}
