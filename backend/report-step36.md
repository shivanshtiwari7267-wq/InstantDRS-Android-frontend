# STEP 36 — Production Deployment Readiness & Environment Separation Report

## A. Current Repository
* **Branch**: None
* **Commit**: N/A
* **Git status**: Not a git repository

## B. Environment Separation
* **Development configuration**: Uses standard Spring Boot defaults (`application.properties`) designed for local, unauthenticated execution with default endpoints.
* **Production configuration**: Provided via the new `application-prod.properties` template. 
* **Environment variables**: Systematically bound for port, database URL, credentials, storage path, CORS origins, and FFmpeg path.
* **Secrets handling**: No secrets are embedded in the source code; they must be provided at runtime via standard environment variables.

## C. Database
* **MySQL configuration**: Configured via the `spring.datasource.*` property family.
* **Production configuration mechanism**: Environment variable injection (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`).
* **Remaining migration considerations**: The application uses `hibernate.ddl-auto`. In the production template, this is set to `validate`. Real production deployments require a dedicated schema migration framework such as Liquibase or Flyway.

## D. Storage
* **Video storage, frames, replay output**: Output routes strictly through `RecordingStorageService.java`.
* **Configurable storage root**: The root path is configured via `STORAGE_PATH` (defaults to `recordings`). 
* **Filesystem safety**: The path traversal protection established in Step 35 (via `resolvePath()`) operates securely off the new dynamically configured root.

## E. FFmpeg
* **Dependency**: Video processing expects an external FFmpeg binary.
* **Executable discovery**: Uses `ffmpeg` command-line fallback by default.
* **Configuration**: Exposed dynamically through `FFMPEG_PATH`.
* **Missing-FFmpeg behavior**: Safely catches `IOException` during `ProcessBuilder` execution and deterministicly flags the `VideoProcessingJob` as `FAILED` with "FFmpeg executable unavailable or IO error" rather than silent hanging.

## F. Frontend
* **VITE_API_BASE_URL**: Properly consumed in API calls and supported in the build pipeline.
* **.env.example**: Present and explicitly documents the empty default fallback configuration.
* **Production build**: Successfully generates static assets using `vite build` without errors.
* **Absence of frontend secrets**: The UI code does not bundle sensitive parameters; only public base URLs are configurable.

## G. CORS
* **Development origin**: Proxied successfully using Vite backend proxy configurations.
* **Production configuration mechanism**: Set safely through the `CORS_ALLOWED_ORIGINS` environment variable within `WebConfig.java`.

## H. Runtime
* **Server port**: Overridable via `SERVER_PORT` environment injection.
* **Startup behavior**: Fast-fails natively on missing critical configuration variables or invalid URLs.
* **Configuration failures**: Fails clearly when database credentials, urls, or storage roots are inaccessible.

## I. Security Regression
* **Path traversal protection**: INTACT
* **Secure upload extension handling**: INTACT
* **Ownership validation**: INTACT
* **Job hierarchy validation**: INTACT
* **Replay directory isolation**: INTACT
* **Frame filename validation**: INTACT
* **CORS configuration**: INTACT
* **Secret protection**: INTACT

## J. Real Video Smoke Test
* **Video**: `recordings/validation/real-cricket-video1.mp4`
* **Processing status**: `COMPLETED`
* **Analysis status**: `COMPLETED`
* **DRS result**: `INSUFFICIENT_DATA`
* **DRS reason**: `BOUNCE_MISSING`
* **Evidence Quality**: `PARTIAL`
* **Evidence Quality reason**: `MULTIPLE_CONFLICTING_CANDIDATES`

## K. Browser Validation
* **Dashboard loads**: PASS
* **Real backend data loads**: PASS
* **DRS card renders**: PASS
* **Evidence Quality renders**: PASS
* **Timeline renders**: PASS
* **Replay unavailable state renders safely**: PASS

## L. Tests
**Backend**:
* **Exact count**: 191
* **Failures**: 0
* **Errors**: 0
* **Skipped**: 0
* **Build status**: SUCCESS

**Frontend**:
* **Exact count**: 22
* **Failures**: 0
* **Errors**: 0
* **Build status**: SUCCESS

## M. Production Readiness Gaps
* **Production Database Provisioning**: Need a managed MySQL instance with secure networking.
* **HTTPS/TLS**: Needs an ingress controller or reverse proxy for termination.
* **Authentication/Authorization**: Completely lacking application security logic.
* **Persistent Storage**: Needs scalable attached network storage (e.g. AWS EFS) mapped to the `STORAGE_PATH`.
* **Schema Migrations**: Need Flyway/Liquibase applied automatically during CI/CD.
* **Monitoring & Metrics**: Missing Actuator, Prometheus endpoints, or structured JSON logging for monitoring.
* **Rate Limiting**: Upload endpoints need DoS protection.

## N. Final Assessment
`PARTIAL`
