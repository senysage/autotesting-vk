import json
from pathlib import Path
from app.main import create_app

if __name__ == "__main__":
    Path("openapi.json").write_text(json.dumps(create_app().openapi(), ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
