package com.preetam.emailvalidator.beans;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

/** Tests for the application configuration. */
@SpringBootTest
@Import(AppConfig.class)
@SuppressWarnings("PMD.AtLeastOneConstructor")
class AppConfigTest {

  /** Injected RestTemplate bean for testing. */
  @Autowired private RestTemplate restTemplate;

  /** Verifies that the RestTemplate bean is correctly loaded into the context. */
  @Test
  void restTemplateBeanShouldBeAvailable() {
    assertThat(restTemplate).isNotNull();
  }
}
