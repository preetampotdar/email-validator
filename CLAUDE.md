# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Full build (compile, test, coverage, Checkstyle, PMD, Spotless)
./gradlew clean build

# Run tests only
./gradlew test

# Run a single test class
./gradlew test --tests "com.preetam.emailvalidator.services.EmailValidatorServiceTest"

# Run a single test method
./gradlew test --tests "com.preetam.emailvalidator.services.EmailValidatorServiceTest.testValidateEmailFullFlowSuccess"

# Run the application (requires DNS_API_KEY env var)
DNS_API_KEY=<your-key> ./gradlew bootRun

# Apply Spotless formatting
./gradlew spotlessApply

# Check for dependency updates
./gradlew dependencyUpdates
```

## Code Quality Gates

All of these must pass for `build` to succeed:

- **JaCoCo**: 100% line and branch coverage is enforced — every new code path needs a test.
- **Checkstyle**: Google Java Style (`config/checkstyle/google_checks.xml`) — no violations allowed.
- **PMD**: Full ruleset across bestpractices, design, codestyle, documentation, errorprone, multithreading, performance, security — no failures.
- **Spotless**: License header (`LICENSE_HEADER`) must be present at the top of every `src/**/*.java` file. Run `spotlessApply` to fix formatting automatically.

## Code Style

Use comments sparingly — only comment complex or non-obvious code. Self-explanatory code should speak for itself.

## Architecture

The app is a single Spring Boot REST service. The request lifecycle is:

1. **`RateLimitingFilter`** (servlet filter) — per-IP rate limiting via Bucket4j (10 req/min). Returns HTTP 429 on breach.
2. **`EmailController`** — `POST /api/v1/email/validate`. Validates the `@NotBlank @Email` request, delegates to the service.
3. **`EmailValidatorService`** — core orchestrator, result is `@Cacheable` keyed by email address (30-min TTL, 10k max entries via Caffeine). Runs four checks in sequence:
   - Syntax via Apache Commons `EmailValidator`
   - Domain safety via `GoogleSafeBrowsingService` (only if syntax is valid)
   - MX record lookup via `DnsLookupService`/`DnsLookupServiceImpl` (dnsjava)
   - Disposable domain check against a hardcoded list in the service
4. Returns `EmailResponse` containing `validSyntax`, `isSafe`, `mxResponse` (with optional `mxFailureReason`), and `isDisposable`.

**Testability pattern**: `EmailValidatorService.getLogger()` is `protected` so tests can spy/override it via Mockito. This pattern is used across services and health indicators.

**DNS abstraction**: `DnsLookupService` (interface) / `DnsLookupServiceImpl` / `LookupFactory` (interface for `Lookup` creation) — the factory layer exists solely to allow Mockito mocking of static DNS calls in tests.

**Health indicators**: `DnsHealthIndicator` tests DNS by querying `google.com` MX records; `CaffeineCacheHealthIndicator` reports cache stats. Both are exposed via `/actuator/health`.

## Environment

The app requires one environment variable:

```
DNS_API_KEY=<Google Safe Browsing API key>
```

This is referenced in `application.properties` as `google.safebrowsing.api.key=${DNS_API_KEY}`. If not set, the Google Safe Browsing check will fail gracefully and `isSafe` will be absent/null in the response.
