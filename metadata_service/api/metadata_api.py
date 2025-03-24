import logging

from flask import Blueprint, jsonify, request

from metadata_service.api.request_models import NameParam, MetadataQuery
from metadata_service.domain import metadata
from metadata_service.domain.version import Version

logger = logging.getLogger()
metadata_api = Blueprint("metadata_api", __name__)


@metadata_api.get("/metadata/data-store")
def get_data_store():
    logger.info("GET /metadata/data-store")

    response = jsonify(metadata.find_all_datastore_versions())
    response.headers.set("content-language", "no")
    return response


@metadata_api.get("/metadata/data-structures/status")
def get_data_structure_current_status():
    validated_query = NameParam(**request.args)
    logger.info(
        f"GET /metadata/data-structures/status with name = {validated_query.names}"
    )
    response = jsonify(
        metadata.find_current_data_structure_status(
            validated_query.get_names_as_list()
        )
    )
    response.headers.set("content-language", "no")
    return response


@metadata_api.post("/metadata/data-structures/status")
def get_data_structure_current_status_as_post():
    validated_body = NameParam(**request.json)

    logger.info(
        f"POST /metadata/data-structures/status with name = {validated_body.names}"
    )
    response = jsonify(
        metadata.find_current_data_structure_status(
            validated_body.get_names_as_list()
        )
    )
    response.headers.set("content-language", "no")
    return response


@metadata_api.get("/metadata/data-structures")
def get_data_structures():
    validated_query = MetadataQuery(**request.args)
    validated_query.include_attributes = True
    logger.info(f"GET /metadata/data-structures with query: {validated_query}")

    response = jsonify(
        metadata.find_data_structures(
            validated_query.names,
            Version(validated_query.version),
            validated_query.include_attributes,
            validated_query.skip_code_lists,
        )
    )
    response.headers.set("content-language", "no")
    return response


@metadata_api.get("/metadata/all-data-structures")
def get_all_data_structures_ever():
    logger.info("GET /metadata/all-data-structures")

    response = jsonify(metadata.find_all_data_structures_ever())
    response.headers.set("content-language", "no")
    return response


@metadata_api.get("/metadata/all")
def get_all_metadata():
    validated_query = MetadataQuery(**request.args)
    logger.info(f"GET /metadata/all with version: {validated_query.version}")

    response = jsonify(
        metadata.find_all_metadata(
            Version(validated_query.version), validated_query.skip_code_lists
        )
    )
    response.headers.set("content-language", "no")
    return response


@metadata_api.get("/languages")
def get_languages():
    logger.info("GET /languages")

    response = jsonify(metadata.find_languages())
    return response
