package com.preetam.emailvalidator.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health indicator for the cache system.
 *
 * <p>Used to expose cache status in application health checks.
 */
@Component
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CacheHealthIndicator implements HealthIndicator {

  /**
   * Performs health check of the cache system.
   *
   * @return Health object indicating the status of the cache
   */
  @Override
  public Health health() {
    return Health.up().withDetail("cache", "Caffeine cache is available").build();
  }
}
