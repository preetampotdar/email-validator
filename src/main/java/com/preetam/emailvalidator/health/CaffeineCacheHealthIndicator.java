/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.health;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;

/**
 * Health indicator for monitoring the Caffeine cache used in the application.
 *
 * <p>This indicator checks whether the cache named "emailValidationCache"
 * is present, and reports its current size as part of the health details.
 */
@Component
public class CaffeineCacheHealthIndicator implements HealthIndicator {

  /** The cache manager responsible for providing the configured Caffeine cache. */
  private final CacheManager cacheManager;

  /**Constructs a {@code CaffeineCacheHealthIndicator}
   * with the given {@link CacheManager}.
   *
   * @param cacheManager the cache manager used
   *                    to retrieve the "emailValidationCache" instance
   */
  public CaffeineCacheHealthIndicator(final CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  /**
   * Returns the {@link CacheManager} used by this indicator.
   *
   * @return the cache manager
   */
  protected CacheManager getCacheManager() {
    return this.cacheManager;
  }

  /**Performs the health check by verifying that the cache exists
   * and reports its size.
   *
   * @return a {@link Health} object representing the
   *        current health status of the cache
   */
  @Override
  public Health health() {
    final Health.Builder healthBuilder;
    final org.springframework.cache.Cache cache = cacheManager.getCache("emailValidationCache");

    if (cache instanceof CaffeineCache caffeineCache) {
      final Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
      final long size = nativeCache.estimatedSize();

      healthBuilder =
          Health.up().withDetail("cacheName", "emailValidationCache").withDetail("entries", size);
    } else {
      healthBuilder = Health.down().withDetail("cache", "emailValidationCache not found");
    }

    return healthBuilder.build();
  }
}
