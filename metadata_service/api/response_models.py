from collections.abc import Callable
from typing import Any
from fastapi import Request
from fastapi.responses import Response, JSONResponse
import msgpack


class MsgPackResponse(Response):
    media_type = "application/x-msgpack"

    def render(self, content) -> bytes:
        compressed_response = msgpack.packb(content, use_bin_type=True)
        return compressed_response or b""


def metadata_response(
    request: Request,
) -> Callable[[Any], JSONResponse | MsgPackResponse]:
    """
    Returns a response function that compresses the response
    if the accept header in the request is applicaion/x-msgpack
    """

    def respond(content):
        accept = request.headers.get("accept", "")
        if "application/x-msgpack" in accept:
            return MsgPackResponse(content)
        return JSONResponse(content)

    return respond

