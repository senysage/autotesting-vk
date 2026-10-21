import json
from pathlib import Path

import pytest
from jsonschema import Draft202012Validator
from app.main import create_app

SPEC = json.loads((Path(__file__).resolve().parents[1] / "openapi.json").read_text())
CASES = [
    ("post", "/tasks", "/tasks", {"title": "Contract"}, 201),
    ("get", "/tasks", "/tasks", None, 200),
    ("get", "/tasks/1", "/tasks/{task_id}", None, 200),
    ("get", "/tasks/999", "/tasks/{task_id}", None, 404),
    ("post", "/tasks", "/tasks", {}, 422),
    ("get", "/tasks?limit=0", "/tasks", None, 422),
    ("get", "/tasks/abc", "/tasks/{task_id}", None, 422),
    ("delete", "/tasks/999", "/tasks/{task_id}", None, 404),
    ("delete", "/tasks/1", "/tasks/{task_id}", None, 204),
    ("delete", "/tasks/abc", "/tasks/{task_id}", None, 422),
]


def test_openapi_snapshot_is_current():
    assert create_app().openapi() == SPEC, "Обновите контракт: python -m scripts.export_openapi"


@pytest.mark.parametrize("method,url,path,body,status", CASES)
def test_response_contract(client, method, url, path, body, status):
    assert client.post("/tasks", json={"title": "Fixture"}).status_code == 201
    response = client.request(method, url, **({"json": body} if body is not None else {}))
    assert response.status_code == status
    contract = SPEC["paths"][path][method]["responses"][str(status)]

    if status == 204:
        assert "content" not in contract
        assert response.content == b""
        return

    assert response.headers["content-type"].split(";")[0] == "application/json"
    schema = {**contract["content"]["application/json"]["schema"], "components": SPEC["components"]}

    Draft202012Validator.check_schema(schema)
    Draft202012Validator(schema).validate(response.json())


def test_contract_rejects_broken_response():
    schema = {"$ref": "#/components/schemas/Task", "components": SPEC["components"]}
    validator = Draft202012Validator(schema)
    assert not validator.is_valid({"id": "wrong", "title": "x", "priority": 6})
