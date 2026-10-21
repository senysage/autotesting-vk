import pytest


def create_task(client, title, priority=1):
    response = client.post("/tasks", json={"title": title, "priority": priority})
    assert response.status_code == 201
    return response.json()


def test_read_specific_task(client):
    tasks = [create_task(client, f"Задача {i}", priority=i) for i in range(1, 3)]
    assert len({task["id"] for task in tasks}) == 2
    for task in tasks:
        response = client.get(f"/tasks/{task['id']}")
        assert response.status_code == 200
        assert response.json() == task


def test_delete_preserves_other_tasks(client):
    tasks = [create_task(client, f"Задача {i}") for i in range(3)]
    response = client.delete(f"/tasks/{tasks[1]['id']}")
    assert response.status_code == 204
    assert client.get(f"/tasks/{tasks[1]['id']}").status_code == 404
    remaining = [tasks[0], tasks[2]]

    response = client.get("/tasks")
    assert response.status_code == 200
    assert response.json() == remaining

    for task in remaining:
        response = client.get(f"/tasks/{task['id']}")
        assert response.status_code == 200
        assert response.json() == task


def test_ids_remain_unique_after_deletion(client):
    first = create_task(client, "Первая")
    deleted = create_task(client, "Удаляемая")
    assert client.delete(f"/tasks/{deleted['id']}").status_code == 204
    third = create_task(client, "Третья")
    fourth = create_task(client, "Четвёртая")
    assert len({task["id"] for task in [first, deleted, third, fourth]}) == 4

    response = client.get("/tasks")
    assert response.status_code == 200
    assert response.json() == [first, third, fourth]


def test_default_list_limit(client):
    tasks = [create_task(client, f"Задача {i}") for i in range(11)]
    response = client.get("/tasks")
    assert response.status_code == 200
    assert response.json() == tasks[:10]

    response = client.get("/tasks", params={"limit": 100})
    assert response.status_code == 200
    assert response.json() == tasks


def test_create_and_read(client):
    response = client.post("/tasks", json={"title": "Изучить REST", "priority": 3})
    assert response.status_code == 201
    task = response.json()
    assert task == {"id": 1, "title": "Изучить REST", "priority": 3}

    response = client.get(f"/tasks/{task['id']}")
    assert response.status_code == 200
    assert response.json() == task


def test_default_priority(client):
    response = client.post("/tasks", json={"title": "Тест"})
    assert response.status_code == 201
    assert response.json()["priority"] == 1


def test_list_limit(client):
    assert client.get("/tasks").json() == []

    for title in ["Первая", "Вторая", "Третья"]:
        assert client.post("/tasks", json={"title": title}).status_code == 201

    response = client.get("/tasks", params={"limit": 2})
    assert response.status_code == 200
    assert [task["title"] for task in response.json()] == ["Первая", "Вторая"]


def test_delete(client):
    task_id = client.post("/tasks", json={"title": "Удалить"}).json()["id"]

    response = client.delete(f"/tasks/{task_id}")
    assert response.status_code == 204
    assert response.content == b""
    assert client.get(f"/tasks/{task_id}").status_code == 404
    assert client.get("/tasks").json() == []
    assert client.delete(f"/tasks/{task_id}").status_code == 404


@pytest.mark.parametrize("method", ["get", "delete"])
def test_missing_task(client, method):
    response = getattr(client, method)("/tasks/999")
    assert response.status_code == 404
    assert response.json() == {"detail": "Task not found"}


@pytest.mark.parametrize("length", [1, 100])
@pytest.mark.parametrize("priority", [1, 5])
def test_valid_boundaries(client, length, priority):
    body = {"title": "x" * length, "priority": priority}
    response = client.post("/tasks", json=body)
    assert response.status_code == 201
    assert response.json() == {"id": 1, **body}


@pytest.mark.parametrize("body,field", [
    ({}, "title"),
    ({"title": ""}, "title"),
    ({"title": " "}, "title"),
    ({"title": "x" * 101}, "title"),
    ({"title": None}, "title"),
    ({"title": 123}, "title"),
    ({"title": "x", "priority": 0}, "priority"),
    ({"title": "x", "priority": 6}, "priority"),
    ({"title": "x", "priority": "3"}, "priority"),
    ({"title": "x", "priority": True}, "priority"),
    ({"title": "x", "priority": 1.5}, "priority"),
    ({"title": "x", "unknown": 1}, "unknown"),
])
def test_invalid_input(client, body, field):
    response = client.post("/tasks", json=body)
    assert response.status_code == 422
    assert any(error["loc"] == ["body", field] for error in response.json()["detail"])
    assert client.get("/tasks").json() == []


@pytest.mark.parametrize("limit", [1, 100])
def test_valid_limit_boundaries(client, limit):
    response = client.get("/tasks", params={"limit": limit})
    assert response.status_code == 200
    assert response.json() == []


@pytest.mark.parametrize("limit", [0, 101, "abc"])
def test_invalid_limit(client, limit):
    response = client.get("/tasks", params={"limit": limit})
    assert response.status_code == 422
    assert response.json()["detail"][0]["loc"] == ["query", "limit"]


def test_invalid_path(client):
    response = client.get("/tasks/abc")
    assert response.status_code == 422
    assert response.json()["detail"][0]["loc"] == ["path", "task_id"]


def test_malformed_json(client):
    response = client.post("/tasks", content='{"title":', headers={"Content-Type": "application/json"})
    assert response.status_code == 422
    assert response.json()["detail"][0]["type"] == "json_invalid"
    assert client.get("/tasks").json() == []


@pytest.mark.parametrize("body", [b"", b"null"], ids=["missing-body", "null-body"])
def test_missing_or_null_body_preserves_tasks(client, body):
    existing = create_task(client, "Существующая задача")
    response = client.post(
        "/tasks", content=body, headers={"Content-Type": "application/json"}
    )
    assert response.status_code == 422
    assert any(error["loc"] == ["body"] for error in response.json()["detail"])

    response = client.get("/tasks")
    assert response.status_code == 200
    assert response.json() == [existing]


def test_missing_task_deletion_preserves_existing_tasks(client):
    tasks = [create_task(client, f"Задача {i}") for i in range(3)]
    missing_id = max(task["id"] for task in tasks) + 1
    response = client.delete(f"/tasks/{missing_id}")
    assert response.status_code == 404
    assert response.json() == {"detail": "Task not found"}

    response = client.get("/tasks")
    assert response.status_code == 200
    assert response.json() == tasks

    for task in tasks:
        response = client.get(f"/tasks/{task['id']}")
        assert response.status_code == 200
        assert response.json() == task
