# 🛡️ SafeGuard SA

**Student Safety Reporting + AI Health Assistant Platform**

![Java](https://img.shields.io/badge/Java-21-red)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0--M1-blue)
![Groq](https://img.shields.io/badge/Groq-Llama%203-orange)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-pgvector-336791)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED)
![Google Cloud](https://img.shields.io/badge/Google%20Cloud-Deployed-4285F4)

> One platform. Two modules. Five team members. Built for student safety and well‑being.

---

## 📌 Live Demo

<a href="https://safeguard-sa.onrender.com">
<div align="center">
  <img src="src\main\resources\static\images\landing_page.png"" alt="Car Secure App Interface" width="600"/>
  <br/>
  <em>AI-Powered Healthcare Chatbot combined with SafeAlert for Crime Prevention</em>
</div>
</a>
---

## ✨ Features

### Module 1: Student Safety Map (Anonymous Tip Reporting)

| Feature | Description |
|---------|-------------|
| 🗺️ **Interactive Safety Map** | Leaflet.js map with colour‑coded pins (red = assault/crime, orange = theft, yellow = suspicious) |
| 📝 **Anonymous Tip Submission** | Submit incident details – **no name, email, or phone** stored by design |
| 🏷️ **Province & Category Filters** | Easily filter tips on the map |
| 🔒 **Admin Moderation** | Review, approve, flag, or delete pending tips |
| (Comming Soon)📧 **Area Alert Emails** | Notify subscribed students when a new tip is approved in their area (Gmail SMTP) |
| 🚨 **Fake Exit Button** | One‑click redirect to Google.com – present on every page for user safety |

### Module 2: AI Health Assistant (RAG + Groq AI)

| Feature | Description |
|---------|-------------|
| 🤖 **Groq Llama 3 Integration** | High-speed inference using Llama 3.3‑70b (via OpenAI-compatible Spring AI provider) |
| 📚 **RAG with MedQA** | Vector similarity search (pgvector) over medical question‑answer dataset |
| ⚡ **Safety Gate** | Scans for emergency keywords (bleeding, chest pain,overdose, etc.) – blocks AI calls during critical events |
| 📖 **Source Citations** | AI response includes references to specific MedQA knowledge chunks retrieved from PostgreSQL |
| 🧑‍⚕️ **Medical Disclaimer** | Permanent disclaimer: *“Always consult a doctor”* |
| 🆘 **Emergency Redirect** | Safety gate triggers a dedicated page with helpline numbers (10111, 112, poison helpline, suicide crisis line) |
| 📜 **Chat History** | Optional user login to save conversation history |

### Admin Dashboard (Unified)

| Feature | Description |
|---------|-------------|
| 📊 **Chart.js Visualizations** | Tips by province (bar chart), tips by category (doughnut), monthly trends (line chart) |
| 🛡️ **Safety Gate Audit Log** | View all emergency‑triggered messages |
| 📌 **Tip Moderation Table** | Inline map preview, approve/flag/delete actions |

---

## 🛠️ Tech Stack

### Backend
- **Java Spring Boot 3** – Main application framework
- **Spring AI** – Connector to Groq
- **Spring Security** – Role‑based access (USER / ADMIN)
- **JPA / Hibernate** – ORM for database entities

### AI & Data
- **Groq Cloud API** – High-speed inference using `llama-3.3-70b-versatile` (or your chosen model)
- **Spring AI** – Connector for Groq (via OpenAI-compatible API)
- **MedQA Dataset** – Open‑source medical Q&A (RAG knowledge base)
- **pgvector** – Vector similarity search extension for PostgreSQL

### Frontend
- **HTML5 / CSS3 / JavaScript** – Plain, no framework
- **Leaflet.js + OpenStreetMap** – Free, no API key needed
- **Chart.js** – Admin dashboard charts

### Database
- **H2 In‑Memory** – Local development (zero setup)
- **PostgreSQL + pgvector** – Production (Supabase or self‑hosted)

### Deployment & Containerization
- **Docker** – Containerized application
- **Google Cloud Run** – Serverless deployment (or Google Compute Engine)
- **Tomcat 10.1** – Embedded (Spring Boot) or external

### Development Environment
- **NetBeans 21+** (Jakarta EE bundle)
- **Maven** – Dependency management

---

## 📁 Project Structure
## 🧪 Testing & Quality Assurance

| Activity | Description | Responsible |
|----------|-------------|-------------|
| Hallucination testing | 50+ Gemini queries to verify RAG accuracy | Team Member E |
| Safety Gate validation | Emergency keyword detection + redirect | Team Member B |
| Moderation workflow | Approve/flag/delete – end‑to‑end | Team Member C & E |
| Responsive UI | Test on desktop, tablet, mobile | All |

## 👥 Team Roles (5 Members)

| Role | Member | Focus Areas |
|------|--------|-------------|
| **Architect** | [S DYWILI](https://github.com/Siph3sihle-Dyw1l1) | Spring Boot skeleton, Security, Deployment (Docker + GCP) |
| **AI Medic** | [MJ KGOMO](https://github.com/mapk6-apl) | Gemini API, Safety Gate, RAG pipeline, MedQA ingestion |
| **Map Specialist** | [KC MASHA](https://github.com/codedbykabelo)  | Leaflet.js, tip submission, geocoding, filters |
| **Data & Stats** | [N CHUMA](https://github.com/chums-glitch) | PostgreSQL + pgvector, repositories, dashboard charts |
| **Security & QA** | [T PALE](https://github.com/Tshephang497) | Spring Security, login, hallucination tests, JUnit |

## 📅 Development Timeline (8 Weeks)

| Phase | Weeks | Deliverables |
|-------|-------|--------------|
| **Skeleton** | 1–2 | GitHub repo, Spring Boot runs on Tomcat, H2 connected |
| **Brains** | 3–4 | Basic Gemini response, static map, MedQA chunking starts |
| **Features** | 5–6 | RAG pipeline, tip submission → map pins, Safety Gate, admin login |
| **Polish & Deploy** | 7–8 | Dashboard completed, Docker + GCP deployment, final report |

## 📄 License

This project is developed for academic purposes as a student team project.

## 🙏 Acknowledgements

- [Groq Cloud](https://console.groq.com) – High-performance AI inference API
- [MedQA Dataset](https://github.com/jind11/MedQA) – Medical QA corpus
- [Leaflet.js](https://leafletjs.com) – Open‑source maps
- [OpenStreetMap](https://openstreetmap.org) – Map tiles
- [pgvector](https://github.com/pgvector/pgvector) – Vector search for PostgreSQL
- [Spring AI](https://spring.io/projects/spring-ai) – AI integration
- [Chart.js](https://www.chartjs.org) – Simple charts
