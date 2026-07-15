<div align="center">

# Knowly

**Everyone knows something. No one knows everything.**

A peer-to-peer learning platform where people connect live to help each other learn — real-world experts, real conversations, no course required.

[🔗 Live Demo](https://app.praveensknowly.in) · [Report a Bug](../../issues) · [Request a Feature](../../issues)

</div>

---

## About

Knowly connects people who need help on a specific skill with someone who's already solved that exact problem — a practicing lawyer, an electrician, a Spring Boot developer, a home cook — in a live, direct conversation instead of a pre-recorded course.

This project was built solo, end to end, as a way to go deep on production-grade backend engineering: authentication, security hardening, database design, and deployment — not just feature building.

## Features

- 🔍 **Skill & expert search** — discover verified experts across tech, legal, finance, cooking, and more
- 👤 **Rated expert profiles** — ratings, languages, and skill breakdowns for every expert
- 💬 **Real-time chat** — message experts directly and get help in a live session
- 🤝 **Networking** — connect with other learners and experts over time
- 🔐 **OAuth2 login** — sign in with Google or GitHub
- 🛎️ **Notifications & settings** — manage your account, email, and preferences
- 📱 **Fully responsive** — designed to work cleanly on mobile and desktop

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot, Spring Security 6 |
| Database | MySQL, Hibernate/JPA, Flyway migrations |
| Frontend | Thymeleaf, vanilla JavaScript (ES6), vanilla CSS |
| Auth | OAuth2 (Google, GitHub), session-based auth |
| Rate Limiting | Bucket4j |
| Email | Brevo (transactional email, OTP flows) |
| Deployment | Docker, Railway |

## Security & Production Readiness

This wasn't left as a demo — it went through a full, self-run security and functionality audit before launch, including:

- Fixed CSRF gaps across modal forms and `fetch()` calls
- Identified and patched an OAuth account-takeover vulnerability
- Closed a signup race condition (non-atomic user creation)
- Added OTP rate limiting on sensitive flows (email change, password reset)
- Server-side message and file upload validation
- Rotated a hardcoded credential found during audit
- Docker `HEALTHCHECK`, graceful shutdown, and Actuator health endpoint for production monitoring
- Persistent volume configuration for user-uploaded files

## Screenshots

> _Add 2–3 screenshots here — homepage, expert search, and a chat/session view work well._

```
<img width="959" height="539" alt="image" src="https://github.com/user-attachments/assets/7a7fbddb-0ed5-4029-a90e-177576f6b4d7" />
<img width="958" height="539" alt="image" src="https://github.com/user-attachments/assets/e1830659-fd3d-4b83-a6ea-da75d754b9e6" />
<img width="958" height="532" alt="image" src="https://github.com/user-attachments/assets/bb2785eb-ef8f-4654-b63f-d36f43053aee" />
```

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8+
- Maven

### Setup

```bash
# Clone the repo
git clone https://github.com/praveensknowly/knowly.git
cd knowly

# Configure environment variables
cp .env.example .env
# Fill in DB credentials, OAuth client IDs/secrets, and Brevo API key

# Run database migrations
./mvnw flyway:migrate

# Start the application
./mvnw spring-boot:run
```

The app will be available at `http://localhost:8080`.

## Architecture Highlights

- **Session lifecycle**: help sessions follow a `PENDING → ACTIVE → EXPIRED → IGNORED` state machine with a scheduled sweep for timeouts, rather than repeated `findAll()` polling.
- **Layered security**: constructor-based dependency injection, method-level authorization, and CSRF protection applied consistently across MVC and API endpoints.
- **Frontend without a framework**: all UI is hand-built with semantic HTML5, vanilla CSS (page-scoped class prefixes to avoid collisions), and vanilla JS — no React/Vue/Bootstrap.

## Contact

Built by **Praveen** — open to backend/full-stack opportunities.

[LinkedIn](#praveen9391)  · [Portfolio](#praveensknowly@gmail.com) · [Email](#praveenrajannapareddy15@gmail.com)<img width="2400" height="3000" alt="knowly-linkedin-post" src="https://github.com/user-attachments/assets/43cfc627-7d8d-49de-8740-a8945b4eae05" />

