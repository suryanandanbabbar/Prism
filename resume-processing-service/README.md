# Resume Processing Service

FastAPI service used by the Spring Boot Job Tracker for resume parsing and optimization.

## Run locally

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python -m spacy download en_core_web_sm
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

Set `OPENAI_API_KEY` to enable LLM-backed optimization. Without it, the service returns a deterministic local tailored draft so the full integration remains testable.
