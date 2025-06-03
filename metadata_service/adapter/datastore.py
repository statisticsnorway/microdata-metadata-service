import json
from typing import Tuple

from metadata_service.config import environment
from metadata_service.domain.version import Version
from metadata_service.exceptions.exceptions import DataNotFoundException

DATASTORE_ROOT_DIR = environment.get("DATASTORE_ROOT_DIR")
NEWEST_CACHE: Tuple[str, dict] = ("", {})
LAST_REQUEST_CACHE: Tuple[str, dict] = ("", {})


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


def get_from_cache(file_version: str) -> dict | None:
    if file_version == NEWEST_CACHE[0]:
        return NEWEST_CACHE[1]
    if file_version == LAST_REQUEST_CACHE[0]:
        return LAST_REQUEST_CACHE[1]
    return None


def update_cache(file_version: str, metadata_all):
    global NEWEST_CACHE
    global LAST_REQUEST_CACHE
    if NEWEST_CACHE[0] == "" or LAST_REQUEST_CACHE[0] == "":
        NEWEST_CACHE = (file_version, metadata_all)
        LAST_REQUEST_CACHE = (file_version, metadata_all)
    elif file_version > NEWEST_CACHE[0]:
        NEWEST_CACHE = (file_version, metadata_all)
    else:
        LAST_REQUEST_CACHE = (file_version, metadata_all)


def get_metadata_all(version: Version) -> dict:
    if version.is_draft():
        file_version = "DRAFT"
    else:
        file_version = version.to_3_underscored()
        metadata_all = get_from_cache(file_version)
        if metadata_all is not None:
            return metadata_all

    metadata_all_file_path = (
        f"{DATASTORE_ROOT_DIR}/datastore/metadata_all__{file_version}.json"
    )
    try:
        with open(metadata_all_file_path, "r", encoding="utf-8") as f:
            metadata_all = json.load(f)
            if not version.is_draft():
                update_cache(file_version, metadata_all)
            return metadata_all
    except FileNotFoundError as e:
        raise DataNotFoundException(
            f"metadata_all for version {version} not found"
        ) from e
