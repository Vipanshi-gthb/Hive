# Project Intelligence Platform

An AI-augmented project management platform that goes beyond task tracking — it aims to *understand* project data well enough to explain project health, surface risks with evidence, answer questions grounded in project documents, and turn meeting notes into reviewable project updates.

> **Status:** Personal learning project, built solo as a full-stack + applied-AI exercise. See [Implemented vs Planned](#implemented-vs-planned) below for an honest breakdown of what's actually working versus what's designed but not yet built.

---

## Table of Contents

- [Overview](#overview)
- [Problem Statement](#problem-statement)
- [Motivation](#motivation)
- [Key Features](#key-features)
- [AI Capabilities](#ai-capabilities)
- [Screenshots](#screenshots)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Major Workflows](#major-workflows)
- [AI Pipelines](#ai-pipelines)
- [RAG Explanation](#rag-explanation)
- [Installation](#installation)
- [Environment Variables](#environment-variables)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [Database Setup](#database-setup)
- [Running Locally](#running-locally)
- [API Overview](#api-overview)
- [Testing](#testing)
- [Deployment](#deployment)
- [Project Structure](#project-structure)
- [Implemented vs Planned](#implemented-vs-planned)
- [Limitations](#limitations)
- [Learning Outcomes](#learning-outcomes)
- [Author](#author)

---

## Overview

Project Intelligence Platform is a full-stack project management tool (think: a focused, single-team Jira/Linear alternative) with three AI pipelines layered on top of a conventional, deterministic project-management core:

1. **AI Project Builder** — turn a plain-language project idea into a reviewable, editable epic/task/milestone plan.
2. **Project Intelligence Engine** — deterministic health scoring, risk detection, workload analysis, and root-cause investigation, explained in natural language by AI.
3. **Project Brain** — a retrieval-augmented (RAG) assistant that answers questions from your project's own documents, with source citations.

The core philosophy: **Plan → Understand → Monitor → Predict → Act → Learn.**

## Problem Statement

Most student and small-team project management tools are glorified to-do lists: they store status but don't help anyone understand *why* a project is behind, *what* is actually at risk, or *what happened* in a meeting three weeks ago. This project explores what a genuinely intelligence-augmented PM tool looks like — one where AI interprets and explains real project data instead of just answering chat questions in a sidebar.

## Motivation

Built as a personal, non-coursework project to learn how a real full-stack application is architected end-to-end — React/Tailwind on the frontend, Java/Spring Boot/PostgreSQL on the backend — and, specifically, how AI can be integrated *meaningfully* into a product's core workflows rather than bolted on as an isolated chatbot feature.

## Key Features

- Project / epic / task / sprint / milestone management with a Kanban workflow
- Task dependency modeling with circular-dependency prevention
- Role-based access control (Project Manager, Developer, Stakeholder)
- Interactive dependency graph visualization
- Risk radar across schedule, workload, dependency, scope, and completion risk
- AI-assisted project planning from a natural-language idea
- AI-explained project health scoring and root-cause analysis
- Document-grounded project knowledge assistant (RAG)
- Meeting-notes-to-project-update extraction with human approval
- Sprint/project retrospective report generation

## AI Capabilities

| Capability | What it does | What AI does vs. what the backend does |
|---|---|---|
| AI Project Builder | Idea → epics/tasks/milestones/dependencies | AI drafts the structure; backend persists only after human approval |
| Health Score & Risk | Numeric project health, risk categories | Backend computes all numbers deterministically; AI explains and recommends |
| Root Cause Investigation | "Why is milestone X delayed?" | Backend traces the dependency/blocker chain; AI narrates the evidence |
| Project Brain (RAG) | Document Q&A with citations | Backend retrieves relevant chunks; AI answers only from retrieved context |
| Meeting Analysis | Transcript → proposed changes | AI extracts structured changes; backend applies only after approval |
| What-If Simulation | Impact of hypothetical changes | Backend propagates the impact through the dependency graph; AI narrates the result |
| Retrospective | End-of-sprint report | Backend aggregates real data; AI drafts the written report from that evidence |

## Screenshots

> _Screenshots will be added here as the UI is completed._
- `![Dashboard](docs/screenshots/dashboard.png)`
- `![Dependency Graph](docs/screenshots/dependency-graph.png)`
- `![Risk Radar](docs/screenshots/risk-radar.png)`
- `![Project Brain Chat](docs/screenshots/project-brain.png)`

## Architecture

See [`ARCHITECTURE.md`](./ARCHITECTURE.md) for the full technical architecture, entity-relationship diagrams, database schema, API structure, and AI pipeline diagrams.

High-level shape:

```
React + Tailwind  →  REST API  →  Spring Boot (Auth / Core PM / Intelligence / AI Orchestration)  →  PostgreSQL + pgvector
                                                                    ↓
                                                              Gemini API
```

## Tech Stack

**Frontend:** React, Tailwind CSS, React Router, Recharts, React Flow, Lucide React
**Backend:** Java, Spring Boot, Spring Security, JWT, Spring Data JPA, Hibernate
**Database:** PostgreSQL, pgvector
**AI:** Google Gemini API (generation + embeddings), custom RAG pipeline
**Tooling:** Git/GitHub, Postman
**Deployment:** Vercel (frontend), Render/Railway (backend), hosted PostgreSQL

## Major Workflows

1. **Plan a project:** describe it in plain language → review AI-generated epics/tasks/milestones → edit as needed → approve to create the real project.
2. **Run a project:** manage tasks on a Kanban board, track dependencies, monitor sprints and milestones.
3. **Understand a project:** check the dashboard for health score, risks, and workload; drill into "why" questions via root-cause investigation.
4. **Ask project questions:** upload documents and ask the Project Brain assistant, with every answer traceable to a source.
5. **Process a meeting:** paste notes/transcript → review AI-extracted decisions and changes → apply or discard.
6. **Reflect:** generate a retrospective report at the end of a sprint or project.

## AI Pipelines

Full pipeline diagrams are in `ARCHITECTURE.md`. Summary:

- **AI Project Builder:** idea → requirement extraction → gap detection → features → epics → tasks → dependency/milestone/timeline suggestions → human review → persist.
- **Project Intelligence:** project data → deterministic metrics/evidence → AI explanation/classification/recommendation.
- **Project Brain (RAG):** documents → chunking → embeddings → pgvector storage → retrieval → grounded, cited answers.
- **Meeting Analysis:** transcript → structured extraction → human review → apply to real entities.
- **What-If Simulation:** hypothetical input → deterministic dependency-graph propagation → AI narration.
- **Retrospective:** aggregated sprint/project data → AI-generated structured report.

## RAG Explanation

The Project Brain feature uses Retrieval-Augmented Generation: uploaded documents are split into chunks, each chunk is embedded into a vector using the Gemini embeddings API, and vectors are stored in PostgreSQL via the `pgvector` extension. When a user asks a question, the question is embedded, the most similar document chunks are retrieved via vector similarity search, and those chunks — not the model's general knowledge — are given to Gemini as context to generate an answer with source citations. If no sufficiently relevant chunks are found, the system says so rather than guessing, to reduce hallucination.

## Installation

### Prerequisites
- Node.js (LTS)
- Java 17+ and Maven
- PostgreSQL with the `pgvector` extension available
- A Gemini API key

### Clone

```
git clone <repo-url>
cd project-intelligence-platform
```

## Environment Variables

Backend (`backend/.env` or equivalent Spring config):

```
DATABASE_URL=jdbc:postgresql://localhost:5432/project_intelligence
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION_MS=3600000
GEMINI_API_KEY=your_gemini_api_key
CORS_ALLOWED_ORIGIN=http://localhost:5173
```

Frontend (`frontend/.env`):

```
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

> Never commit `.env` files. A `.env.example` with placeholder values is provided instead.

## Backend Setup

```
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

## Frontend Setup

```
cd frontend
npm install
npm run dev
```

## Database Setup

1. Create the database: `createdb project_intelligence`
2. Enable pgvector: `CREATE EXTENSION IF NOT EXISTS vector;`
3. Run migrations (Flyway/Liquibase, run automatically on backend startup, or manually per the migration tool's CLI).

## Running Locally

1. Start PostgreSQL.
2. Start the backend (`./mvnw spring-boot:run`) — default port `8080`.
3. Start the frontend (`npm run dev`) — default port `5173`.
4. Visit `http://localhost:5173`, register an account, and start creating a project.

## API Overview

All endpoints are under `/api/v1`. Representative groups: `/auth`, `/projects`, `/projects/{id}/epics|tasks|sprints|milestones`, `/tasks/{id}/dependencies`, `/projects/{id}/dashboard|health-score|risks|workload`, `/projects/{id}/ai/plan`, `/projects/{id}/documents`, `/projects/{id}/brain/query`, `/projects/{id}/meetings`, `/projects/{id}/simulate`, `/projects/{id}/retrospective`. Full request/response contracts are documented via Postman collection (see `/docs/postman`).

## Testing

- Backend: `./mvnw test` (unit + integration tests)
- Manual test scripts for each feature phase are documented alongside the implementation roadmap.

## Deployment

- **Frontend:** deployed to Vercel, pointed at the production API base URL.
- **Backend:** deployed to Render/Railway with production environment variables configured.
- **Database:** hosted PostgreSQL with `pgvector` enabled.

See `ARCHITECTURE.md` §12 for the full deployment architecture.

## Project Structure

```
project-intelligence-platform/
├── backend/
│   └── src/main/java/.../{controller,service,repository,entity,dto,ai,config,exception}
├── frontend/
│   └── src/{pages,components,hooks,api,context}
├── docs/
│   ├── screenshots/
│   └── postman/
├── ROADMAP.md
├── ARCHITECTURE.md
└── README.md
```

## Implemented vs Planned

- **Implemented:** _(update as phases complete — this section should always reflect actual current state, not the target design)_
- **Planned (designed, not yet built):** see `ROADMAP.md` for the full phase-by-phase plan.
- **Future / stretch:** What-If Simulation, historical metric trend tracking, retrospective export, advanced deadline-prediction modeling.

## Limitations

- Single-team scope — not designed for multi-tenant/organization-level usage.
- AI-generated content depends on Gemini API availability and quota; the deterministic core continues to function if the AI layer is unavailable.
- Vector search is tuned for small-to-moderate document collections, not enterprise-scale document repositories.
- Built and tested by a single developer; test coverage prioritizes core and authorization flows over exhaustive edge cases.

## Learning Outcomes

Through this project: full-stack architecture with a clean separation of frontend/backend/AI/database concerns; JWT-based authentication and role-based authorization; relational schema design including graph-shaped data (dependencies); retrieval-augmented generation with vector databases; designing AI features around deterministic-core/AI-explains and human-approval patterns rather than letting an LLM freely mutate data; and managing a multi-week solo project using AI-assisted ("vibe") coding responsibly and incrementally.

## Author

_Author section placeholder — name, links, contact._
