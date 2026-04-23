# Resume Processing API Contracts

## Spring Boot Public APIs

### Upload CV

`POST /api/v1/cvs?userId={userId}`

Multipart form fields:

- `file`: PDF or DOCX resume.

Response includes stored file metadata plus parsed fields:

- `parsedName`
- `parsedSkills`
- `parsedExperience`
- `parsedProjects`
- `parseStatus`: `PARSED`, `FAILED`, or `PENDING`
- `parseError`

### Optimize Resume

`POST /resume/optimize`

Request:

```json
{
  "cvDocumentId": 1,
  "jobDescription": "We need a Java Spring Boot engineer...",
  "versionLabel": "Backend Engineer - Acme"
}
```

Response:

```json
{
  "id": 2,
  "cvDocumentId": 1,
  "versionNumber": 2,
  "versionLabel": "Backend Engineer - Acme",
  "jobDescription": "We need a Java Spring Boot engineer...",
  "content": "Tailored resume text...",
  "createdAt": "2026-04-23T09:15:00Z"
}
```

### List Resume Versions

`GET /resume/versions?cvDocumentId={cvDocumentId}`

Returns resume versions in descending version order. Version `1` is created from the original parsed upload; later versions are optimizer outputs.

## FastAPI Internal APIs

### Health

`GET /health`

Response:

```json
{
  "status": "ok"
}
```

### Parse Resume

`POST /parse`

Multipart form fields:

- `file`: PDF or DOCX resume.

Response:

```json
{
  "name": "Jane Doe",
  "skills": ["java", "postgresql", "spring boot"],
  "experience": ["Senior Backend Engineer at Acme"],
  "projects": ["Job Tracker System"],
  "rawText": "Full extracted resume text..."
}
```

### Optimize Resume

`POST /optimize`

Request:

```json
{
  "job_description": "We need a Java Spring Boot engineer...",
  "original_resume": "Full original resume text..."
}
```

Response:

```json
{
  "tailored_resume": "Tailored resume text..."
}
```
