# Prism

Prism is a production-style Job Tracking and Automation Platform designed to streamline the modern job application workflow. The system combines intelligent scraping, automation pipelines, backend APIs, and tracking dashboards into a unified ecosystem that helps users manage opportunities efficiently.

The project integrates Java-based enterprise backend services with Python-powered automation modules to create a scalable and modular architecture suitable for real-world deployment scenarios.

---

# Features

- Centralized job application tracking
- Automated job scraping pipelines
- RESTful backend APIs
- Modular microservice-style architecture
- FastAPI automation services
- Spring Boot backend services
- Scalable database integration
- Secure authentication and session handling
- Dashboard-ready structured data
- Extensible architecture for AI integrations

---

# Tech Stack

## Backend
- Java
- Spring Boot
- Spring Security
- Hibernate / JPA

## Automation Services
- Python
- FastAPI
- Web Scraping Modules
- Task Automation Pipelines

## Database
- MySQL / PostgreSQL

## Tools & Technologies
- Maven
- Git
- GitHub
- REST APIs
- JSON
- Docker (Optional Deployment)

---

# System Architecture

```text
                    +----------------------+
                    |      Frontend        |
                    |  Dashboard / Client  |
                    +----------+-----------+
                               |
                               v
                    +----------------------+
                    |    Spring Boot API   |
                    | Authentication Layer |
                    | Business Logic Layer |
                    +----------+-----------+
                               |
              -----------------------------------------
              |                                       |
              v                                       v
+----------------------------+        +----------------------------+
|      Database Layer        |        |   FastAPI Automation Hub   |
| MySQL / PostgreSQL Storage |        | Scraping & Automation APIs |
+----------------------------+        +-------------+--------------+
                                                    |
                                                    v
                                      +----------------------------+
                                      | External Job Platforms     |
                                      | LinkedIn / Portals / APIs  |
                                      +----------------------------+
