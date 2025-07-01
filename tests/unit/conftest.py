import pytest

from metadata_service.app import app
from fastapi import testclient


@pytest.fixture(scope="session")
def test_app():
    yield testclient.TestClient(app)
