from __future__ import annotations

import os
import time
from pathlib import Path

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field


app = FastAPI(title="Auto Apply Service", version="1.0.0")


class ApplyRequest(BaseModel):
    job_application_id: int
    company: str = Field(min_length=1, max_length=160)
    role: str = Field(min_length=1, max_length=160)
    job_link: str = Field(min_length=1, max_length=1000)
    source: str = Field(min_length=1, max_length=120)
    resume_file_path: str = Field(min_length=1, max_length=1000)
    payload: str | None = None


class ApplyResponse(BaseModel):
    success: bool
    message: str


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/apply", response_model=ApplyResponse)
def apply_to_job(request: ApplyRequest) -> ApplyResponse:
    validate_request(request)
    use_selenium = os.getenv("AUTO_APPLY_USE_SELENIUM", "false").lower() == "true"
    if not use_selenium:
        return simulate_apply(request)
    return selenium_apply(request)


def validate_request(request: ApplyRequest) -> None:
    if not request.job_link.startswith(("http://", "https://")):
        raise HTTPException(status_code=400, detail="Job link must be an absolute URL")
    if not Path(request.resume_file_path).exists():
        raise HTTPException(status_code=400, detail="Resume file path does not exist")


def simulate_apply(request: ApplyRequest) -> ApplyResponse:
    time.sleep(float(os.getenv("AUTO_APPLY_SIMULATION_DELAY_SECONDS", "0.4")))
    return ApplyResponse(
        success=True,
        message=(
            "Simulated application submitted for "
            f"{request.role} at {request.company} via {request.source}"
        ),
    )


def selenium_apply(request: ApplyRequest) -> ApplyResponse:
    try:
        from selenium import webdriver
        from selenium.common.exceptions import WebDriverException
        from selenium.webdriver.common.by import By
        from selenium.webdriver.support import expected_conditions as expected
        from selenium.webdriver.support.ui import WebDriverWait
    except ImportError as exc:
        raise HTTPException(status_code=500, detail="Selenium is not installed") from exc

    options = webdriver.ChromeOptions()
    if os.getenv("AUTO_APPLY_HEADLESS", "true").lower() == "true":
        options.add_argument("--headless=new")
    options.add_argument("--disable-gpu")
    options.add_argument("--no-sandbox")

    driver = None
    try:
        driver = webdriver.Chrome(options=options)
        driver.set_page_load_timeout(int(os.getenv("AUTO_APPLY_PAGE_TIMEOUT_SECONDS", "30")))
        driver.get(request.job_link)

        wait = WebDriverWait(driver, int(os.getenv("AUTO_APPLY_WAIT_SECONDS", "10")))
        fill_text_fields(driver, request)
        upload_resume(driver, request.resume_file_path)
        click_submit_if_present(wait, By, expected)

        return ApplyResponse(success=True, message="Selenium application flow completed")
    except WebDriverException as exc:
        return ApplyResponse(success=False, message=f"Selenium automation failed: {exc.msg}")
    except Exception as exc:
        return ApplyResponse(success=False, message=f"Application automation failed: {exc}")
    finally:
        if driver is not None:
            driver.quit()


def fill_text_fields(driver, request: ApplyRequest) -> None:
    field_values = {
        "name": os.getenv("AUTO_APPLY_CANDIDATE_NAME", "Candidate"),
        "email": os.getenv("AUTO_APPLY_CANDIDATE_EMAIL", "candidate@example.com"),
        "phone": os.getenv("AUTO_APPLY_CANDIDATE_PHONE", "0000000000"),
    }
    for key, value in field_values.items():
        for selector in (
            f"input[name*='{key}' i]",
            f"input[id*='{key}' i]",
            f"textarea[name*='{key}' i]",
        ):
            elements = driver.find_elements("css selector", selector)
            for element in elements:
                if element.is_displayed() and element.is_enabled():
                    element.clear()
                    element.send_keys(value)


def upload_resume(driver, resume_path: str) -> None:
    file_inputs = driver.find_elements("css selector", "input[type='file']")
    for file_input in file_inputs:
        if file_input.is_enabled():
            file_input.send_keys(str(Path(resume_path).absolute()))
            return


def click_submit_if_present(wait, by, expected) -> None:
    selectors = [
        "button[type='submit']",
        "input[type='submit']",
        "button[aria-label*='submit' i]",
        "button[aria-label*='apply' i]",
    ]
    for selector in selectors:
        try:
            button = wait.until(expected.element_to_be_clickable((by.CSS_SELECTOR, selector)))
            button.click()
            return
        except Exception:
            continue
