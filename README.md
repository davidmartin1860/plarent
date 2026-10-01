# Plarent

Plarent helps people manage and care for their houseplants: plant profiles, care
schedules and logs, watering/fertilising/pruning/repotting reminders, photos,
shared households, push notifications, and (eventually) IoT/sensor integrations.

This repository is the initial foundation for that product: a small, working
vertical slice — **list plants, view a plant, create a plant** — implemented
across every target platform, with the conventions the rest of the product will
be built on.

## Stack

| Layer         | Technology                                         |
|---------------|-----------------------------------------------------|
| API           | Kotlin, Spring Boot, PostgreSQL, Flyway, Gradle      |
| Web           | Next.js (App Router), TypeScript, Tailwind CSS       |
| Mobile shared | Kotlin Multiplatform (networking + models)           |
| Android       | Jetpack Compose, consuming the shared KMP module     |
| iOS           | SwiftUI, native networking (see `mobile/iosApp`)     |

## Repository layout

```
plarent/
├── compose.yaml          # Local dev stack: postgres + backend + web
├── .env.example           # Copy to .env to configure the stack
├── backend/                # Kotlin + Spring Boot REST API
├── web/                    # Next.js + TypeScript + Tailwind web app
└── mobile/
    ├── shared/             # Kotlin Multiplatform module (models, API client)
    ├── androidApp/         # Jetpack Compose app (open mobile/ in Android Studio)
    └── iosApp/             # SwiftUI app (see mobile/iosApp/README.md)
```

Android and iOS are intentionally **not** containerized — they run through
Android Studio and Xcode respectively. Postgres, the backend, and the web app
run via Docker Compose.

## API contract (v1 vertical slice)

All clients (web, Android, iOS) talk to the same REST contract:

- `GET /api/plants` → `200`, array of `Plant`
- `GET /api/plants/{id}` → `200 Plant` or `404`
- `POST /api/plants` → body below → `201 Plant` (or `400` on validation failure)

```jsonc
// Plant
{
  "id": "uuid",
  "name": "Monstera",
  "species": "Monstera deliciosa",
  "location": "Living room window",  // nullable
  "acquiredDate": "2024-03-01",      // nullable, YYYY-MM-DD
  "notes": "Loves bright indirect light", // nullable
  "createdAt": "2026-10-01T17:14:12.675Z",
  "updatedAt": "2026-10-01T17:14:12.675Z"
}
```

```jsonc
// POST /api/plants request body
{
  "name": "Monstera",      // required, 1-100 chars
  "species": "...",        // required, 1-150 chars
  "location": "...",       // optional
  "acquiredDate": "...",   // optional, YYYY-MM-DD
  "notes": "..."           // optional
}
```

## Running the local stack (backend + web + Postgres)

```bash
cp .env.example .env
docker compose up --build
```

Or, with [Task](https://taskfile.dev) installed:

```bash
task up
```

Run `task --list` to see all available tasks (backend/web dev, tests, etc.).

- Web: http://localhost:3000
- API: http://localhost:8080/api/plants
- Postgres: localhost:5432 (credentials from `.env`)

The web app is a Next.js server that calls the backend over the Docker network
(`API_URL=http://backend:8080/api`), using Server Components for reads and a
Server Action for creating a plant — there's no client-side API key or CORS
configuration to manage for the web app.

### Hot reload

`task up` runs the stack with hot reload. It layers `compose.dev.yaml` on top
of `compose.yaml` and runs it with
[Compose Watch](https://docs.docker.com/compose/how-tos/file-watch/)
(`docker compose up --watch`, requires Docker Compose v2.27+):

- **Backend**: changes under `backend/src` are synced into the container, a
  continuous Gradle build recompiles them, and Spring Boot DevTools restarts
  the app (a few seconds).
- **Web**: changes under `web/` are synced into the container, where `next dev`
  applies them via Fast Refresh.
- Changing `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`,
  `package.json` or `package-lock.json` rebuilds the affected image.

Plain `docker compose up --build` (and `task up:detached` / `task backend:up`)
still runs the production images (fat jar / Next.js standalone) without hot
reload.

### Backend only (for mobile development against a local API)

```bash
docker compose up -d postgres backend
```

The API is then reachable at `http://localhost:8080/api` from your Mac
(e.g. the iOS Simulator), or at `http://10.0.2.2:8080/api` from the Android
emulator (see `mobile/androidApp` — the emulator's virtual network can't see
`localhost` the way the host machine does).

## Backend development

```bash
cd backend
./gradlew bootRun      # run locally against the dockerized Postgres
./gradlew test         # unit tests (MockMvc) + Testcontainers integration test
```

Schema changes go in `backend/src/main/resources/db/migration` as new Flyway
migrations (`V2__...sql`, `V3__...sql`, ...); `hibernate.ddl-auto` is set to
`validate`, so Hibernate never auto-generates schema.

## Web development

```bash
cd web
npm install
npm run dev   # expects the API at http://localhost:8080/api (API_URL env var)
```

## Mobile development

Open `mobile/` in **Android Studio** to build/run the Android app (it includes
the `:shared` Kotlin Multiplatform module and the `:androidApp` Compose app).

For iOS, see `mobile/iosApp/README.md` — it's generated with
[XcodeGen](https://github.com/yonaskolb/XcodeGen) from `project.yml` rather
than a committed `.xcodeproj`, so the project structure stays readable in git.

## Conventions

- **Package naming**: `com.plarent.*` across the backend, shared KMP module,
  and Android app (`com.plarent.backend`, `com.plarent.shared`,
  `com.plarent.android`). iOS uses bundle id `com.plarent.ios`.
- **Migrations, not `ddl-auto`**: all schema changes are explicit Flyway SQL
  migrations, checked into source control.
- **Server-driven web rendering**: the web app avoids a public client-side API
  surface where possible, preferring Server Components/Actions.
- **No Docker for mobile**: Android/iOS are developed and run through their
  native IDEs, not containers.
