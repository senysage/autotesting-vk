from fastapi import FastAPI, HTTPException, Query, Response
from pydantic import BaseModel, ConfigDict, Field


class TaskInput(BaseModel):
    model_config = ConfigDict(extra="forbid", strict=True)
    title: str = Field(min_length=1, max_length=100, pattern=r"\S")
    priority: int = Field(default=1, ge=1, le=5)


class Task(TaskInput):
    id: int


class Error(BaseModel):
    detail: str


def create_app() -> FastAPI:
    app = FastAPI(title="Task API", version="1.0.0", description="Учебный REST API")
    tasks: dict[int, Task] = {}
    next_id = 1

    @app.post("/tasks", response_model=Task, status_code=201)
    def create_task(body: TaskInput):
        nonlocal next_id
        task = Task(id=next_id, **body.model_dump())
        tasks[next_id] = task
        next_id += 1

        return task

    @app.get("/tasks", response_model=list[Task])
    def list_tasks(limit: int = Query(default=10, ge=1, le=100)):
        return list(tasks.values())[:limit]

    @app.get("/tasks/{task_id}", response_model=Task, responses={404: {"model": Error}})
    def get_task(task_id: int):
        if task_id not in tasks:
            raise HTTPException(404, "Task not found")

        return tasks[task_id]

    @app.delete("/tasks/{task_id}", status_code=204, responses={404: {"model": Error}})
    def delete_task(task_id: int):
        if task_id not in tasks:
            raise HTTPException(404, "Task not found")
        del tasks[task_id]

        return Response(status_code=204)

    return app


app = create_app()
