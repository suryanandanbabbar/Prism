# Auto Apply Service

FastAPI service used by Spring only after a user approves a pending application.

The default mode is simulation (`AUTO_APPLY_USE_SELENIUM=false`) so the workflow can run safely in development. To enable browser automation, install Chrome/ChromeDriver and set `AUTO_APPLY_USE_SELENIUM=true`.

## Run locally

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8020
```

Useful environment variables:

- `AUTO_APPLY_USE_SELENIUM=false`
- `AUTO_APPLY_HEADLESS=true`
- `AUTO_APPLY_CANDIDATE_NAME=Candidate`
- `AUTO_APPLY_CANDIDATE_EMAIL=candidate@example.com`
- `AUTO_APPLY_CANDIDATE_PHONE=0000000000`
