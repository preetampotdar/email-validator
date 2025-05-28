/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for application-wide Spring beans.
 * Defines beans such as RestTemplate for
 * dependency injection.
 */
@Configuration
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class AppConfig {

  /**
   * Bean definition for RestTemplate to perform REST operations.
   *
   * @return a new instance of RestTemplate
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
