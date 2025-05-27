package com.preetam.emailvalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main entry point for the Email Validator Spring Boot application.
 * This class bootstraps the
 * Spring context and enables caching.
 */
@SpringBootApplication
@EnableCaching
@SuppressWarnings("PMD.UseUtilityClass")
public class EmailValidatorApplication {

  /**
   * Main method to start the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(final String[] args) {
    SpringApplication.run(EmailValidatorApplication.class, args);
  }
}
