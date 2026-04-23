# Job Scraper Service

FastAPI service used by the Spring Boot Job Tracker to discover jobs from LinkedIn, Indeed, and Naukri.

By default the service uses simulated HTML parsed with BeautifulSoup so local development is reliable and does not depend on external sites. To attempt live HTTP scraping, set `SCRAPER_USE_NETWORK=true`. Live scraping may be blocked by platform protections or terms, so failures are returned in the `errors` array and the service falls back to simulated results.

## Run locally

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8010
```

Useful environment variables:

- `SCRAPER_USE_NETWORK=false`
- `SCRAPER_MIN_DELAY_SECONDS=0.2`
- `SCRAPER_MAX_DELAY_SECONDS=0.8`
- `SCRAPER_HTTP_TIMEOUT_SECONDS=12`
