# Help Guide - Email Validator Application

## What is this application?
This is a Spring Boot-based Email Validator service that verifies if an email address is syntactically correct, checks if the domain has valid MX DNS records, and determines if the email domain is disposable.

---

## How to Use the API

### Validate Email Address

**Endpoint:**
```

POST /api/v1/email/validate

````

**Request Body:**
```json
{
  "email": "user@example.com"
}
````

**Response Body:**

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

---

## What do the response fields mean?

* `email`: The input email address you provided.
* `validSyntax`: Indicates if the email format is valid.
* `mx.hasMx`: Whether the domain has valid MX (mail exchange) records.
* `mx.hasMxFailureDescription`: If MX check fails, this contains an error message.
* `isDisposable`: True if the domain is known to be disposable (temporary email services).

---

## Caching

The service caches email validation results for 30 minutes to improve response times and reduce DNS lookups.

---

## Health Check Endpoints

* `/actuator/health`: Shows overall application health status.
* `/actuator/health/cache`: Shows the health status of the caching system.
* `/actuator/health/dns`: Shows the health status of DNS MX record lookups.

---

## Troubleshooting

### Common Issues:

* **Email validation returns false even for valid emails:**

    * Check network connectivity for DNS MX record lookup.
    * Verify if the email domain is currently reachable.

* **Disposable email detection may not catch all temporary domains:**

    * The disposable domain list is hardcoded. You can customize it by modifying the service class.

* **Health checks show DOWN status:**

    * Check application logs for detailed errors.
    * Ensure DNS lookups are working from your environment.

---

## Configuration

You can customize cache settings in the `application.properties` file:

```properties
spring.cache.cache-names=emailValidationCache
spring.cache.caffeine.spec=expireAfterWrite=30m,maximumSize=10000
```

---

## How to run tests

Run the unit and integration tests with:

```bash
./mvnw test
```

or

```bash
./gradlew test
```

---

## Contact / Support

If you have questions or need support, please reach out to:

* Developer: Preetam Potdar
* Email: [preetam1@outlook.com](mailto:preetam1@outlook.com)

---

Thank you for using the Email Validator Application!
