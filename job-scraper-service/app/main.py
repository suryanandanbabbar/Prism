from __future__ import annotations

import os
import random
import time
from dataclasses import dataclass
from typing import Callable
from urllib.parse import quote_plus

import requests
from bs4 import BeautifulSoup
from fastapi import FastAPI
from pydantic import BaseModel, Field


app = FastAPI(title="Job Scraper Service", version="1.0.0")

USER_AGENTS = [
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_6) AppleWebKit/537.36 Chrome/120.0 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/121.0 Safari/537.36",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/122.0 Safari/537.36",
]


class ScrapeRequest(BaseModel):
    role: str = Field(min_length=1, max_length=120)
    location: str = Field(min_length=1, max_length=120)
    skills: list[str] = Field(default_factory=list)


class ScrapedJob(BaseModel):
    company: str
    role: str
    link: str
    description: str
    source: str


class ScrapeResponse(BaseModel):
    jobs: list[ScrapedJob] = Field(default_factory=list)
    errors: list[str] = Field(default_factory=list)


@dataclass(frozen=True)
class Platform:
    name: str
    search_url_builder: Callable[[ScrapeRequest], str]
    parser: Callable[[str, ScrapeRequest], list[ScrapedJob]]


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/scrape", response_model=ScrapeResponse)
def scrape_jobs(request: ScrapeRequest) -> ScrapeResponse:
    response = ScrapeResponse()
    use_network = os.getenv("SCRAPER_USE_NETWORK", "false").lower() == "true"

    for platform in platforms():
        delay()
        try:
            html = fetch(platform.search_url_builder(request)) if use_network else simulated_html(platform.name, request)
            response.jobs.extend(platform.parser(html, request))
        except Exception as exc:
            response.errors.append(f"{platform.name}: {exc}")
            response.jobs.extend(parse_simulated(simulated_html(platform.name, request), request, platform.name))

    return response


def platforms() -> list[Platform]:
    return [
        Platform("LinkedIn", linkedin_url, lambda html, request: parse_generic_cards(html, request, "LinkedIn")),
        Platform("Indeed", indeed_url, lambda html, request: parse_generic_cards(html, request, "Indeed")),
        Platform("Naukri", naukri_url, lambda html, request: parse_generic_cards(html, request, "Naukri")),
    ]


def linkedin_url(request: ScrapeRequest) -> str:
    return (
        "https://www.linkedin.com/jobs/search/?keywords="
        f"{quote_plus(request.role)}&location={quote_plus(request.location)}"
    )


def indeed_url(request: ScrapeRequest) -> str:
    return f"https://www.indeed.com/jobs?q={quote_plus(request.role)}&l={quote_plus(request.location)}"


def naukri_url(request: ScrapeRequest) -> str:
    role = quote_plus(request.role.lower().replace(" ", "-"))
    location = quote_plus(request.location.lower().replace(" ", "-"))
    return f"https://www.naukri.com/{role}-jobs-in-{location}"


def fetch(url: str) -> str:
    headers = {
        "User-Agent": random.choice(USER_AGENTS),
        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Accept-Language": "en-US,en;q=0.9",
    }
    timeout = float(os.getenv("SCRAPER_HTTP_TIMEOUT_SECONDS", "12"))
    response = requests.get(url, headers=headers, timeout=timeout)
    response.raise_for_status()
    return response.text


def delay() -> None:
    min_delay = float(os.getenv("SCRAPER_MIN_DELAY_SECONDS", "0.2"))
    max_delay = float(os.getenv("SCRAPER_MAX_DELAY_SECONDS", "0.8"))
    time.sleep(random.uniform(min_delay, max_delay))


def parse_generic_cards(html: str, request: ScrapeRequest, source: str) -> list[ScrapedJob]:
    jobs = parse_simulated(html, request, source)
    if jobs:
        return jobs

    soup = BeautifulSoup(html, "html.parser")
    candidates = soup.select("a[href], div, li")
    parsed: list[ScrapedJob] = []
    for element in candidates[:80]:
        text = " ".join(element.get_text(" ", strip=True).split())
        if not text or request.role.lower() not in text.lower():
            continue
        link = element.get("href") or ""
        company = extract_company(text)
        parsed.append(
            ScrapedJob(
                company=company,
                role=request.role,
                link=normalize_link(link, source),
                description=text[:1000],
                source=source,
            )
        )
        if len(parsed) >= 10:
            break
    return parsed


def parse_simulated(html: str, request: ScrapeRequest, source: str) -> list[ScrapedJob]:
    soup = BeautifulSoup(html, "html.parser")
    jobs: list[ScrapedJob] = []
    for card in soup.select("[data-job-card]"):
        jobs.append(
            ScrapedJob(
                company=card.select_one("[data-company]").get_text(strip=True),
                role=card.select_one("[data-role]").get_text(strip=True),
                link=card.select_one("a[data-link]")["href"],
                description=card.select_one("[data-description]").get_text(" ", strip=True),
                source=source,
            )
        )
    return jobs


def simulated_html(source: str, request: ScrapeRequest) -> str:
    skills = ", ".join(request.skills[:4]) if request.skills else "backend engineering"
    slug = quote_plus(f"{source} {request.role} {request.location}".lower())
    return f"""
    <html><body>
      <article data-job-card>
        <h2 data-role>{request.role}</h2>
        <span data-company>{source} Partner Labs</span>
        <a data-link href="https://jobs.example.com/{slug}/1">Apply</a>
        <p data-description>{request.role} role in {request.location} using {skills}.</p>
      </article>
      <article data-job-card>
        <h2 data-role>Senior {request.role}</h2>
        <span data-company>{source} Talent Network</span>
        <a data-link href="https://jobs.example.com/{slug}/2">Apply</a>
        <p data-description>Senior opening for {request.role}; preferred skills include {skills}.</p>
      </article>
    </body></html>
    """


def extract_company(text: str) -> str:
    pieces = [piece.strip() for piece in text.split("-") if piece.strip()]
    return pieces[1][:160] if len(pieces) > 1 else "Unknown Company"


def normalize_link(link: str, source: str) -> str:
    if link.startswith("http"):
        return link
    base_urls = {
        "LinkedIn": "https://www.linkedin.com",
        "Indeed": "https://www.indeed.com",
        "Naukri": "https://www.naukri.com",
    }
    return base_urls.get(source, "").rstrip("/") + "/" + link.lstrip("/")
