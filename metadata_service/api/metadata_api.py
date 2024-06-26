import logging

from flask import Blueprint, jsonify
from flask_pydantic import validate

from metadata_service.api.request_models import NameParam, MetadataQuery
from metadata_service.domain import metadata
from metadata_service.domain.version import Version

logger = logging.getLogger()
metadata_api = Blueprint("metadata_api", __name__)


@metadata_api.get("/metadata/data-store")
@validate()
def get_data_store():
    logger.info("GET /metadata/data-store")

    response = jsonify(metadata.find_all_datastore_versions())
    response.headers.set("content-language", "no")
    return response


@metadata_api.get("/metadata/data-structures/status")
@validate()
def get_data_structure_current_status(query: NameParam):
    logger.info(
        f"GET /metadata/data-structures/status with name = {query.names}"
    )
    response = jsonify(
        metadata.find_current_data_structure_status(query.get_names_as_list())
    )
    response.headers.set("content-language", "no")
    return response


@metadata_api.post("/metadata/data-structures/status")
@validate()
def get_data_structure_current_status_as_post(body: NameParam):
    logger.info(
        f"POST /metadata/data-structures/status with name = {body.names}"
    )
    response = jsonify(
        metadata.find_current_data_structure_status(body.get_names_as_list())
    )
    response.headers.set("content-language", "no")
    return response


@metadata_api.get("/metadata/data-structures")
@validate()
def get_data_structures(query: MetadataQuery):
    query.include_attributes = True
    logger.info(f"GET /metadata/data-structures with query: {query}")

    response = jsonify(
        metadata.find_data_structures(
            query.names,
            Version(query.version),
            query.include_attributes,
            query.skip_code_lists,
        )
    )
    response.headers.set("content-language", "no")
    return response


@metadata_api.get("/metadata/all-data-structures")
@validate()
def get_all_data_structures_ever():
    logger.info("GET /metadata/all-data-structures")

    response = jsonify(metadata.find_all_data_structures_ever())
    response.headers.set("content-language", "no")
    return response


@metadata_api.get("/metadata/all")
@validate()
def get_all_metadata(query: MetadataQuery):
    logger.info(f"GET /metadata/all with version: {query.version}")

    response = jsonify(
        metadata.find_all_metadata(
            Version(query.version), query.skip_code_lists
        )
    )
    response.headers.set("content-language", "no")
    return response


@metadata_api.get("/languages")
@validate()
def get_languages():
    logger.info("GET /languages")

    response = jsonify(metadata.find_languages())
    return response
