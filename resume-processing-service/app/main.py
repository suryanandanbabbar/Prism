from __future__ import annotations

import os
import re
from io import BytesIO
from typing import Iterable

import fitz
import spacy
from docx import Document
from fastapi import FastAPI, File, HTTPException, UploadFile
from openai import OpenAI
from pydantic import BaseModel, Field


app = FastAPI(title="Resume Processing Service", version="1.0.0")

ALLOWED_CONTENT_TYPES = {
    "application/pdf",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
}

KNOWN_SKILLS = {
    "aws",
    "azure",
    "docker",
    "fastapi",
    "git",
    "hibernate",
    "java",
    "javascript",
    "jenkins",
    "kafka",
    "kubernetes",
    "microservices",
    "mongodb",
    "mysql",
    "postgresql",
    "python",
    "react",
    "redis",
    "spring boot",
    "sql",
    "typescript",
}

SECTION_HEADINGS = {
    "summary",
    "objective",
    "skills",
    "technical skills",
    "experience",
    "work experience",
    "employment",
    "projects",
    "education",
    "certifications",
}


class ParsedResumeResponse(BaseModel):
    name: str | None = None
    skills: list[str] = Field(default_factory=list)
    experience: list[str] = Field(default_factory=list)
    projects: list[str] = Field(default_factory=list)
    rawText: str


class OptimizeRequest(BaseModel):
    job_description: str = Field(min_length=1, max_length=20000)
    original_resume: str = Field(min_length=1, max_length=50000)


class OptimizeResponse(BaseModel):
    tailored_resume: str


try:
    NLP = spacy.load("en_core_web_sm")
except OSError:
    NLP = spacy.blank("en")


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/parse", response_model=ParsedResumeResponse)
async def parse_resume(file: UploadFile = File(...)) -> ParsedResumeResponse:
    if file.content_type not in ALLOWED_CONTENT_TYPES:
        raise HTTPException(status_code=400, detail="Only PDF and DOCX files are supported")

    content = await file.read()
    if not content:
        raise HTTPException(status_code=400, detail="Resume file is empty")

    text = extract_text(content, file.filename or "", file.content_type or "")
    if not text.strip():
        raise HTTPException(status_code=422, detail="Could not extract text from resume")

    normalized_text = normalize_text(text)
    return ParsedResumeResponse(
        name=extract_name(normalized_text),
        skills=extract_skills(normalized_text),
        experience=extract_section(normalized_text, {"experience", "work experience", "employment"}),
        projects=extract_section(normalized_text, {"projects"}),
        rawText=normalized_text,
    )


@app.post("/optimize", response_model=OptimizeResponse)
def optimize_resume(request: OptimizeRequest) -> OptimizeResponse:
    api_key = os.getenv("OPENAI_API_KEY")
    if api_key:
        try:
            tailored_resume = optimize_with_openai(request, api_key)
        except Exception as exc:
            raise HTTPException(status_code=502, detail="LLM resume optimizer failed") from exc
        if not tailored_resume.strip():
            raise HTTPException(status_code=502, detail="LLM resume optimizer returned an empty response")
        return OptimizeResponse(tailored_resume=tailored_resume)
    return OptimizeResponse(tailored_resume=optimize_locally(request))


def extract_text(content: bytes, filename: str, content_type: str) -> str:
    if content_type == "application/pdf" or filename.lower().endswith(".pdf"):
        return extract_pdf_text(content)
    if content_type == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" or filename.lower().endswith(
        ".docx"
    ):
        return extract_docx_text(content)
    raise HTTPException(status_code=400, detail="Unsupported resume file type")


def extract_pdf_text(content: bytes) -> str:
    try:
        with fitz.open(stream=content, filetype="pdf") as document:
            return "\n".join(page.get_text("text") for page in document)
    except Exception as exc:
        raise HTTPException(status_code=422, detail="Could not parse PDF resume") from exc


def extract_docx_text(content: bytes) -> str:
    try:
        document = Document(BytesIO(content))
        return "\n".join(paragraph.text for paragraph in document.paragraphs)
    except Exception as exc:
        raise HTTPException(status_code=422, detail="Could not parse DOCX resume") from exc


def normalize_text(text: str) -> str:
    lines = [re.sub(r"\s+", " ", line).strip() for line in text.splitlines()]
    return "\n".join(line for line in lines if line)


def extract_name(text: str) -> str | None:
    doc = NLP(text[:1500])
    for entity in doc.ents:
        if entity.label_ == "PERSON" and 2 <= len(entity.text) <= 80:
            return entity.text.strip()

    for line in text.splitlines()[:8]:
        lowered = line.lower()
        if any(token in lowered for token in ("resume", "curriculum", "email", "phone", "@")):
            continue
        if 2 <= len(line) <= 80 and not re.search(r"\d", line):
            return line
    return None


def extract_skills(text: str) -> list[str]:
    lowered = text.lower()
    found = [skill for skill in KNOWN_SKILLS if re.search(rf"\b{re.escape(skill)}\b", lowered)]
    return sorted(found, key=str.lower)


def extract_section(text: str, headings: set[str], max_items: int = 12) -> list[str]:
    lines = text.splitlines()
    capturing = False
    items: list[str] = []

    for line in lines:
        heading = line.strip().lower().rstrip(":")
        if heading in headings:
            capturing = True
            continue
        if capturing and heading in SECTION_HEADINGS:
            break
        if capturing and line.strip():
            cleaned = re.sub(r"^[\-*]\s*", "", line.strip())
            items.append(cleaned)
            if len(items) >= max_items:
                break

    return compact_items(items)


def compact_items(items: Iterable[str]) -> list[str]:
    compacted: list[str] = []
    seen: set[str] = set()
    for item in items:
        key = item.lower()
        if len(item) < 3 or key in seen:
            continue
        compacted.append(item)
        seen.add(key)
    return compacted


def optimize_with_openai(request: OptimizeRequest, api_key: str) -> str:
    client = OpenAI(api_key=api_key, timeout=float(os.getenv("OPENAI_TIMEOUT_SECONDS", "25")))
    model = os.getenv("OPENAI_MODEL", "gpt-4o-mini")
    completion = client.chat.completions.create(
        model=model,
        temperature=0.2,
        messages=[
            {
                "role": "system",
                "content": (
                    "You tailor resumes for a specific job while preserving factual accuracy. "
                    "Do not invent employers, credentials, dates, degrees, or projects. "
                    "Return only the tailored resume text."
                ),
            },
            {
                "role": "user",
                "content": (
                    "Job description:\n"
                    f"{request.job_description}\n\n"
                    "Original resume:\n"
                    f"{request.original_resume}"
                ),
            },
        ],
    )
    return completion.choices[0].message.content or ""


def optimize_locally(request: OptimizeRequest) -> str:
    keywords = extract_keywords(request.job_description)
    keyword_line = ", ".join(keywords[:18]) if keywords else "Role-aligned experience"
    return (
        "TAILORED RESUME\n\n"
        "Target Keywords\n"
        f"{keyword_line}\n\n"
        "Profile Summary\n"
        "Candidate profile tailored to the supplied job description, emphasizing relevant skills, "
        "delivery impact, and project ownership without adding unsupported claims.\n\n"
        "Original Resume Content\n"
        f"{request.original_resume}"
    )


def extract_keywords(text: str) -> list[str]:
    words = re.findall(r"[A-Za-z][A-Za-z+#.-]{2,}", text.lower())
    stop_words = {
        "and",
        "are",
        "for",
        "the",
        "with",
        "you",
        "your",
        "will",
        "job",
        "role",
        "from",
        "this",
        "that",
    }
    counts: dict[str, int] = {}
    for word in words:
        if word in stop_words:
            continue
        counts[word] = counts.get(word, 0) + 1
    return [word for word, _ in sorted(counts.items(), key=lambda item: (-item[1], item[0]))]
