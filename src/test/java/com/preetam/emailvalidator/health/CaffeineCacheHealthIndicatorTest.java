/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Unit tests for {@link CaffeineCacheHealthIndicator}.
 *
 * <p>Verifies the health status and details when
 * the cache is present or missing, and ensures the
 * cache manager is correctly injected.
 */
@SpringBootTest(classes = CaffeineCacheHealthIndicator.class)
@SuppressWarnings("PMD.AtLeastOneConstructor")
class CaffeineCacheHealthIndicatorTest {

  /** Caffeine cache name. */
  private static final String CACHE_NAME = "emailValidationCache";

  /** Mocked CacheManager to simulate cache presence or absence. */
  @MockitoBean private CacheManager cacheManager;

  /** The health indicator under test. */
  @Autowired private CaffeineCacheHealthIndicator healthIndicator;

  @Test
  @SuppressWarnings("PMD.LawOfDemeter")
  void healthReturnsUpStatusWhenCachePresent() {
    final Cache<Object, Object> cache = Caffeine.newBuilder().build();
    cache.put("key", "value");
    final CaffeineCache caffeineCache = new CaffeineCache(CACHE_NAME, cache);

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(caffeineCache);

    final Health health = healthIndicator.health();
    final String statusCode = health.getStatus().getCode();

    assertThat(statusCode).isEqualTo("UP");
  }

  @Test
  void healthDetailsContainCacheNameWhenCachePresent() {
    final Cache<Object, Object> cache = Caffeine.newBuilder().build();
    cache.put("key", "value");
    final CaffeineCache caffeineCache = new CaffeineCache(CACHE_NAME, cache);

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(caffeineCache);

    final Health health = healthIndicator.health();

    assertThat(health.getDetails()).containsEntry("cacheName", CACHE_NAME);
  }

  @Test
  void healthDetailsContainEntriesCountWhenCachePresent() {
    final Cache<Object, Object> cache = Caffeine.newBuilder().build();
    cache.put("key", "value");
    final CaffeineCache caffeineCache = new CaffeineCache(CACHE_NAME, cache);

    when(cacheManager.getCache(CACHE_NAME)).thenReturn(caffeineCache);

    final Health health = healthIndicator.health();

    assertThat(health.getDetails().get("entries")).isEqualTo(1L);
  }

  @Test
  @SuppressWarnings("PMD.LawOfDemeter")
  void healthReturnsDownStatusWhenCacheMissing() {
    when(cacheManager.getCache(CACHE_NAME)).thenReturn(null);

    final Health health = healthIndicator.health();
    final String statusCode = health.getStatus().getCode();

    assertThat(statusCode).isEqualTo("DOWN");
  }

  @Test
  void healthDetailsContainCacheMissingMessageWhenCacheMissing() {
    when(cacheManager.getCache(CACHE_NAME)).thenReturn(null);

    final Health health = healthIndicator.health();

    assertThat(health.getDetails()).containsEntry("cache", CACHE_NAME + " not found");
  }

  @Test
  void cacheManagerIsInjectedCorrectly() {
    assertThat(healthIndicator.getCacheManager()).isSameAs(cacheManager);
  }
}
