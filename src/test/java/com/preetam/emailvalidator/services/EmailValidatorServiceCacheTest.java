/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.preetam.emailvalidator.models.EmailResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/**Integration tests for {@link EmailValidatorService} to verify caching behavior.
 *
 * <p> Uses {@link SpringBootTest} to load the full application context and
 * {@link AutoConfigureCache}
 * to enable and test Spring's caching mechanism for email validation.
 */
@SpringBootTest
@AutoConfigureCache
@SuppressWarnings("PMD")
public class EmailValidatorServiceCacheTest {

  @MockitoSpyBean private EmailValidatorService validatorService;

  @Test
  public void testValidateEmailIsCached() {
    String testEmail = "test@example.com";

    EmailResponse response1 = validatorService.validateEmail(testEmail);

    EmailResponse response2 = validatorService.validateEmail(testEmail);

    assertThat(response1).isEqualTo(response2);

    verify(validatorService, times(1)).validateEmail(testEmail);
  }
}
