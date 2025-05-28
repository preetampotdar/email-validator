/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.models;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Unit tests for {@link EmailRequest}, verifying email field validation. */
@SpringBootTest
@SuppressWarnings("PMD.AtLeastOneConstructor")
class EmailRequestTest {

  /** Validator used to evaluate JSR-380 (Bean Validation) constraints. */
  @Autowired private Validator validator;

  @Test
  void validEmailPassesValidation() {
    final EmailRequest request = new EmailRequest();
    request.setEmail("test@example.com");

    final Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void blankEmailFailsValidation() {
    final EmailRequest request = new EmailRequest();
    request.setEmail("");

    final Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

    final boolean hasBlankViolation =
        violations.stream().anyMatch(v -> v.getMessage().contains("must not be blank"));

    assertThat(hasBlankViolation).isTrue();
  }

  @Test
  void invalidEmailFormatFailsValidation() {
    final EmailRequest request = new EmailRequest();
    request.setEmail("invalid-email");

    final Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

    final boolean violation =
        violations.stream()
            .anyMatch(v -> v.getMessage().contains("must be a well-formed email address"));

    assertThat(violation).isTrue();
  }

  @Test
  void nullEmailFailsValidation() {
    final EmailRequest request = new EmailRequest();
    request.setEmail(null);

    final Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

    final boolean hasNullViolation =
        violations.stream().anyMatch(v -> v.getMessage().contains("must not be blank"));

    assertThat(hasNullViolation).isTrue();
  }
}
