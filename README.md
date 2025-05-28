# Email Validator Application

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
- Java 17+
- Spring Boot 3.x
- Spring Web
- Spring Boot Actuator
- Caffeine Cache
- Apache Commons Validator
- dnsjava (for DNS lookups)
- JUnit 5 & Mockito (for testing)
- Lombok

## Getting Started

### Prerequisites
- Java 17 or higher installed
- Maven or Gradle build tool
- Internet connection (for DNS lookups)

### Build and Run
```bash
./mvnw clean install
./mvnw spring-boot:run
````

or using Gradle:

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
  "mx": {
    "hasMx": true,
    "hasMxFailureDescription": null
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
./mvnw test
```

or

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