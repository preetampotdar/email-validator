/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.health;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for {@link CacheHealthIndicator}.
 * Verifies that the health indicator correctly reports
 * the cache status and details.
 */
@SpringBootTest(classes = CacheHealthIndicator.class)
@SuppressWarnings("PMD.AtLeastOneConstructor")
class CacheHealthIndicatorTest {

  /** Instance of CacheHealthIndicator to be tested. */
  @Autowired private CacheHealthIndicator indicator;

  /** Test that health status is UP. */
  @Test
  @SuppressWarnings("PMD.LawOfDemeter")
  void healthReturnsStatusUp() {
    final Health health = indicator.health();
    assertThat(health.getStatus().getCode()).isEqualTo("UP");
  }

  /** Test that health details contain cache availability info. */
  @Test
  void healthContainsCacheAvailabilityDetail() {
    final Health health = indicator.health();
    assertThat(health.getDetails()).containsEntry("cache", "Caffeine cache is available");
  }
}
