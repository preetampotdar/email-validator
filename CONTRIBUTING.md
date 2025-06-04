# Contributing to Email Validator

Thank you for your interest in contributing to the Email Validator project! We welcome contributions of all kinds — whether it's fixing bugs, improving documentation, or suggesting new features.

## How to Contribute

### 1. Fork the repository
Click the **Fork** button on the top-right of the GitHub page to create your own copy of the repository.

### 2. Clone your fork locally
```bash
git clone https://github.com/preetampotdar/email-validator.git
cd email-validator
````

### 3. Create a new branch

Use a descriptive branch name for your changes:

```bash
git checkout -b feature/your-feature-name
```

### 4. Make your changes

* Follow the existing coding style (Java 17, using Lombok, etc.).
* Keep your changes focused and well-scoped.
* Write meaningful commit messages.

### 5. Test your changes

Make sure all tests pass before submitting a pull request.

```bash
./gradlew test
```

### 6. Commit and push

```bash
git add .
git commit -m "Add detailed description of your changes"
git push origin feature/your-feature-name
```

### 7. Open a pull request

Go to your fork on GitHub and open a pull request against the `main` branch of this repository.

---

## Code Style and Quality

* The project uses Java 17.
* Please maintain the existing formatting and conventions.
* The project enforces code quality using:

  * Checkstyle (Google Java Style Guide)
  * PMD rules
  * Spotless formatting
* Make sure your code passes all quality checks before submitting.

---

## Testing

* Write unit tests for any new features or bug fixes.
* Run all tests locally with `./gradlew test`.
* The project uses JUnit 5, Mockito, and AssertJ for testing.

---

## Reporting Issues

If you find any bugs or have feature requests, please use the [GitHub Issues](https://github.com/preetampotdar/email-validator/issues) page to report them.

Please include:

* A clear description of the problem or feature.
* Steps to reproduce (for bugs).
* Any relevant logs or screenshots.

---

## License

By contributing, you agree that your contributions will be licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

---

Thank you for helping improve Email Validator! 🎉

```
