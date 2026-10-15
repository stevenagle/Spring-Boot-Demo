# Spring Boot full stack demo

<p align="center">
    <img src="https://skillicons.dev/icons?i=java,spring,gradle,react,ts,js,vite,html,css,git,idea,vscode" alt="Java, Spring, Gradle, React, TypeScript, JavaScript, Vite, HTML, CSS, Git, IntelliJ IDEA, and VS Code" />
</p>

A small account portal built with React, TypeScript, and a Spring Boot REST API.
The backend keeps a controller → service → repository architecture with JPA and an
in-memory H2 database. The UI demonstrates account creation, lookup, editing, and deletion.

## Run locally

Requirements: JDK 25 (Homebrew `openjdk@25` works) and Node.js 22 (22.22.2+),
24 (24.15+), or 26+ (matching the frontend test tooling’s supported release lines).
Use an active Node LTS release for development and CI. The checked-in Gradle wrapper
runs on Java 25; no system Gradle installation is required.

Run these commands from the repository root. Start the backend:

```sh
cd backend
./gradlew bootRun
```

In another terminal at the repository root, start the frontend:

```sh
cd frontend
npm ci
npm run dev
```

Open **http://localhost:5173/login**. Choose **alpha001** to explore a seeded profile,
or create an account with sample data. Ten sample accounts are loaded at startup.
The in-memory database resets when the backend stops.

Vite proxies `/api` to `http://127.0.0.1:8080`. No environment file is needed.
For a separate API host, copy `frontend/.env.example` to `frontend/.env.local` and set
`VITE_API_URL` to the complete profile collection URL, such as
`http://localhost:8080/api/v1/demo/users`.

## Architecture and conventions

- Controllers own HTTP status codes and accept validated request records.
- Services return response records, enforce validation, and own transaction boundaries.
- Repositories persist entities; JPA entities are never serialized directly by the API.
- PATCH uses a single explicit replacement operation. The merged profile is validated
  before the entity is changed, using the same constraints as account creation.
- Errors use RFC 9457 Problem Details. Field validation includes an `errors` map.
- The database enforces required columns and unique usernames; Hibernate validates the schema.
- Session storage remembers only the selected username. The account page always loads a
  fresh profile from the API. Successful writes return the updated representation.

Usernames contain 1–32 ASCII letters or digits. State values use two uppercase letters;
ZIP codes use five digits or ZIP+4. Email and address fields have bounded lengths.
Usernames are case-sensitive. Unknown JSON fields are rejected.

## API

Base path: `/api/v1/demo/users`

| Method | Path | Result |
| --- | --- | --- |
| GET | `/` or the collection path without a trailing slash | `200` and an array sorted by username |
| GET | `/{username}` | `200` and a profile; `404` when absent |
| POST | collection path | `201`, the created profile, and a `Location` header |
| PATCH | `/{username}` | `200` and the updated profile |
| DELETE | `/{username}` | `204` with no body |

Create input contains `username`, `emailAddress`, `streetAddress`, `city`, `state`, and `zipCode`.
PATCH example:

```json
{ "op": "replace", "path": "city", "value": "Chicago" }
```

Invalid requests return `400`; duplicate usernames return `409`.
Successful POST and PATCH responses contain JSON profiles.

## Local security settings

This is an **unauthenticated demo**, not a production identity system. Selecting a
username does not establish an authenticated session or ownership of the profile.
There is no password collection. Use synthetic data only.

The API binds to loopback by default. H2 console and API documentation are disabled;
Actuator exposes only health, without details. API responses disable caching, and
browser headers prevent MIME sniffing and framing. CORS allows only configured origins,
methods, and the Content-Type header, without credentialed browser requests.

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_ADDRESS` | `127.0.0.1` | Backend bind address |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Comma-separated browser origin allowlist |
| `SPRING_DATASOURCE_USERNAME` | `sa` | Local H2 username |
| `SPRING_DATASOURCE_PASSWORD` | empty | Local H2 password |
| `API_DOCS_ENABLED` | `false` | Enable local `/swagger-ui.html` and `/v3/api-docs` |

CORS is a browser access policy, not authorization. Before deploying this outside a
local demo, add server-side authentication and per-profile authorization, HTTPS,
appropriate CSRF protection for cookie authentication, a persistent database with
versioned migrations, and deployment-specific secrets and controls.

## Verify changes

```sh
cd backend
./gradlew check bootJar javadoc
```

Backend checks cover service behavior, validation boundaries, database constraints,
HTTP errors, CORS, and complete account lifecycles. Tests use rollback isolation or
explicit cleanup for committed writes, and distinct in-memory databases for separate
application contexts. Mockito runs as an
explicit Java agent. JaCoCo HTML reports are generated at
`backend/build/reports/jacoco/test/html/index.html`.

```sh
cd frontend
npm ci
npm run lint
npm run format:check
npm test
npm run build
```

Frontend tests cover account lookup and creation, errors and retry, renaming, deletion
confirmation, and HTTP response handling. Use `npm run test:watch` while developing and
`npm run format` to apply formatting. The npm lockfile and Gradle distribution checksum
are checked in for repeatable installs. No hosting or deployment is configured.
