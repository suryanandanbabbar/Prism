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

## Job Scraper APIs

### Trigger Job Scrape

`POST /jobs/scrape`

Request:

```json
{
  "jobPreferenceId": 1
}
```

## Auto Apply Approval APIs

### List Pending Applications

`GET /applications/pending`

Returns jobs that passed the match threshold and are waiting for explicit user approval.

### Approve Application

`POST /applications/{id}/approve`

Only applications in `PENDING_APPROVAL` can be approved. Spring calls the Auto Apply service, stores an automation log, and updates status to `APPLIED` only when the automation service reports success.

### Reject Application

`POST /applications/{id}/reject`

Marks the pending application as `REJECTED` and does not call automation.

### Application Logs

`GET /applications/{id}/logs`

Returns success/failure logs for approvals, rejections, and automation attempts.

### Internal Auto Apply Service

`POST http://localhost:8020/apply`

Request:

```json
{
  "job_application_id": 12,
  "company": "Acme",
  "role": "Backend Engineer",
  "job_link": "https://jobs.example.com/acme/backend",
  "source": "LinkedIn",
  "resume_file_path": "/absolute/path/to/resume.pdf",
  "payload": "{\"matchScore\":0.8}"
}
```

Response:

```json
{
  "success": true,
  "message": "Simulated application submitted for Backend Engineer at Acme via LinkedIn"
}
```

Response:

```json
{
  "fetchedCount": 6,
  "savedCount": 6,
  "duplicateCount": 0,
  "savedJobs": [],
  "errors": []
}
```

Spring reads the `JobPreference`, calls the scraper service, computes `SHA-256(company + role + link)`, and stores new rows in `job_applications` with status `DISCOVERED`.

### List Discovered Jobs

`GET /jobs/discovered`

Returns discovered jobs from `job_applications`.

### Internal Scraper Service

`POST http://localhost:8010/scrape`

Request:

```json
{
  "role": "Backend Engineer",
  "location": "Bengaluru",
  "skills": ["Java", "Spring Boot", "PostgreSQL"]
}
```

Response:

```json
{
  "jobs": [
    {
      "company": "LinkedIn Partner Labs",
      "role": "Backend Engineer",
      "link": "https://jobs.example.com/linkedin-backend-engineer-bengaluru/1",
      "description": "Backend Engineer role in Bengaluru using Java, Spring Boot.",
      "source": "LinkedIn"
    }
  ],
  "errors": []
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
