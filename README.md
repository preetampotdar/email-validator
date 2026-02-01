# Email Validator Application

[![Java CI with Gradle](https://github.com/preetampotdar/email-validator/actions/workflows/gradle.yml/badge.svg)](https://github.com/preetampotdar/email-validator/actions/workflows/gradle.yml)

[![Render](https://img.shields.io/badge/Deployed%20on-Render-3f3f3f?logo=render&logoColor=white)](https://email-validator-7nfm.onrender.com/actuator/health)

## Overview
This Spring Boot application provides a REST API to validate email addresses. It checks for valid email syntax, disposable email domains, and MX DNS records.

## Features
- Validate email syntax using Apache Commons Validator.
- Check if an email domain is disposable (e.g., mailinator.com).
- Verify MX DNS records for email domains.
- Caching of validation results using Caffeine for improved performance.
- Health check endpoints for API and downstream services (DNS, cache).
- Integration with Spring Boot Actuator for monitoring.

## Technologies Used
- Java 25
- Spring Boot 4.x
- Spring Web
- Spring Boot Actuator
- Caffeine Cache
- Apache Commons Validator
- dnsjava (for DNS lookups)
- JUnit 5 & Mockito (for testing)
- Lombok

## Getting Started

### Prerequisites
- Java 25 or higher installed
- Gradle build tool
- Internet connection (for DNS lookups)

### Build and Run
```bash
./gradlew clean build
./gradlew bootRun
```

### API Usage

#### Validate Email

**Endpoint:** `POST /api/v1/email/validate`

**Request:**

```json
{
  "email": "user@example.com"
}
```

**Response:**

```json
{
  "email": "user@example.com",
  "validSyntax": true,
  "isSafe": true,
  "mxResponse": {
    "hasMx": true
  },
  "isDisposable": false
}
```

### Caching

Validation results are cached for 30 minutes using Caffeine Cache to reduce DNS queries and improve performance.

### Health Checks

* `/actuator/health` - Overall application health
* `/actuator/health/cache` - Cache health indicator
* `/actuator/health/dns` - DNS MX record health indicator

## Testing

Unit tests cover the service logic, controller endpoints, cache behavior, and health indicators.

Run tests with:

```bash
./gradlew test
```

## Configuration

Application properties (e.g., cache settings) can be customized in `src/main/resources/application.properties`.

Example cache config:

```properties
spring.cache.cache-names=emailValidationCache
spring.cache.caffeine.spec=expireAfterWrite=30m,maximumSize=10000
```

## Notes

* The app relies on external DNS servers for MX record lookups; network issues may affect validation.
* Disposable email domain list is hardcoded but can be extended or replaced with third-party services.

## License

This project is licensed under the [Apache License 2.0](LICENSE).

---

*Created by Preetam Potdar*
