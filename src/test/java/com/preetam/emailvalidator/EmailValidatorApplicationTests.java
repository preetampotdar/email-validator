package com.preetam.emailvalidator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/** Basic tests for the Email Validator Application context and main method. */
@SpringBootTest
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.JUnitTestsShouldIncludeAssert"})
class EmailValidatorApplicationTests {

  /** Verifies that the Spring application context loads without issues. */
  @Test
  void contextLoads() {
    // This test passes if the application context starts successfully.
  }

  /** Tests that the main method runs without throwing exceptions. */
  @Test
  void mainMethodRunsWithoutErrors() {
    EmailValidatorApplication.main(new String[] {});
  }
}
