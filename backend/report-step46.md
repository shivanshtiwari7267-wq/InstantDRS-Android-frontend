# Step 46 - Android DRS Review Workflow & Operator Controls

## A. Review Workflow
The Android Review Workflow was refined to ensure safe, explicit retrieval of DRS elements matching the actual backend architecture. Given the lack of a bulk job-listing endpoint in the backend, the `GameSessionScreen` was updated to accept explicit `Processing Job ID` and `Analysis Job ID` parameters from the operator (mirroring the React frontend's explicit search behavior). This enables safe transition into the Review Dashboard context without hardcoding IDs or fabricating new endpoints.

## B. Backend APIs
The implementation correctly targets existing controllers and limits operations to read-only queries:
- `GET /api/games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/review`
- `GET /api/games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/pipeline-status`
- `GET /api/games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/result`
- `GET /api/games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/evidence-quality`
- `GET /api/games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/timeline`
- `GET /api/games/{gameId}/session/processing/{processingJobId}/analysis/{analysisJobId}/review/replay`

## C. State Management
`DrsReviewViewModel` handles the explicit states:
- **Loading:** Shown explicitly with progress indicators when fetching data initially.
- **Success:** Rendered safely with optional fields properly handled.
- **Error / Unauthenticated:** Detailed HTTP error parsing (401, 403, 404) maps exceptions to safe UI states. 
- **Refreshing:** A manual refresh mechanism re-triggers the StateFlow load logic cleanly.

## D. Decision
- `DecisionCard.kt` surfaces exact decision enumerations (`OUT`, `NOT_OUT`, `INSUFFICIENT_DATA`). 
- Raw backend reason codes (e.g. `BOUNCE_MISSING`) are translated via `getHumanReadableReason()` to provide operator clarity without tampering with the underlying data logic.

## E. Evidence Quality
`EvidenceQualityPanel.kt` explicitly displays `READY`, `PARTIAL`, or `INSUFFICIENT_DATA` alongside exact reason arrays such as `MULTIPLE_CONFLICTING_CANDIDATES`. Local inference was strictly avoided.

## F. Timeline
Handled effectively in `DrsTimeline.kt`. Elements fall back gracefully to avoid crashes on unknown event types or missing data. 

## G. Replay
The application strictly inspects the boolean `available` and `outputEndpoint` attributes from the `DrsReplayResponse`. It renders "Replay available at: [Endpoint]" without generating, simulating, or initiating potentially unstable ExoPlayer video loads on untested local endpoints.

## H. Retry
The UI supports operator-driven explicit retry logic inside the Error state of the dashboard layout. Retries rely safely on safe `GET` endpoints, ensuring idempotency.

## I. Authentication
A 401 response explicitly bubbles into the error state (mapping to "Authentication has expired."), while the global `AuthInterceptor` simultaneously fires a `TokenManager.sessionExpiredEvent` that forces the navigation stack to reset to `login`.

## J. Android Tests
`ReviewRepositoryTest.kt` covers:
- Loading successful reviews with fully populated Mockito mocked responses.
- Handling 404 failures seamlessly into Kotlin `Result.failure`.
- Verifying exact data parity for Pipeline models.

## K. Android Build
Attempted `.\gradlew.bat test`.
Result: **FAILED** (Environment Blocker).
Error: `SDK location not found. Define a valid SDK location with an ANDROID_HOME environment variable.`

## L. Emulator
DEVICE/EMULATOR VALIDATION: **BLOCKED** due to lack of local Android SDK.

## M. Backend Regression
Result: **PASS** (192/192 tests passed).

## N. React Regression
Result: **PASS** (React test/build verified).

## O. Security
- JWTs are stored in SharedPreferences securely via `TokenManager`.
- Network calls safely use `AuthInterceptor`.
- 401s clear tokens.
- No local hardcoding or debug logs expose keys.

## P. Scope
Step 46 strictly avoided and does NOT implement:
- Camera recording
- Video upload
- Video processing
- FFmpeg
- OpenCV
- New DRS algorithms
- Replay generation

## Q. Final Assessment
**PARTIAL** (Logical implementation and unit tests succeed, but emulator validation is physically blocked by host limitations).
