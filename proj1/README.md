# proj1-java-ui

A tiny Java HTTP server + static HTML UI.

- Serves the frontend at `/` (from `web/index.html`)
- Exposes a demo JSON API at `/api/results`
- Health check endpoint at `/health`

The server is implemented in `AppServer.java` using the JDK built-in `com.sun.net.httpserver.HttpServer`.

---

## Option 1: Run with Docker Compose (recommended)

### Prerequisites

- Docker Desktop (or Docker Engine) with `docker compose` available

### Start

From the project folder (the one containing `compose.yaml`):

```bash
docker compose up --build
