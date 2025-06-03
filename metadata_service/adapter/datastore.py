import json
from functools import lru_cache

from metadata_service.config import environment
from metadata_service.domain.version import Version
from metadata_service.exceptions.exceptions import DataNotFoundException

DATASTORE_ROOT_DIR = environment.get("DATASTORE_ROOT_DIR")


def get_draft_version() -> dict:
    json_file = f"{DATASTORE_ROOT_DIR}/datastore/draft_version.json"
    with open(json_file, encoding="utf-8") as f:
        return json.load(f)


def get_datastore_versions() -> dict:
    datastore_versions_json = (
        f"{DATASTORE_ROOT_DIR}/datastore/datastore_versions.json"
    )
    with open(datastore_versions_json, encoding="utf-8") as f:
        return json.load(f)


@lru_cache(maxsize=32)
def get_metadata_all(version: Version) -> str:
    if version.is_draft():
        file_version = "DRAFT"
    else:
        file_version = version.to_3_underscored()

    metadata_all_file_path = (
        f"{DATASTORE_ROOT_DIR}/datastore/metadata_all__{file_version}.json"
    )
    try:
        with open(metadata_all_file_path, "r", encoding="utf-8") as f:
            return json.load(f)
    except FileNotFoundError as e:
        raise DataNotFoundException(
            f"metadata_all for version {version} not found"
        ) from e
