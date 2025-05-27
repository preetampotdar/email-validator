package com.preetam.emailvalidator.beans;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

/**
 * Unit tests for {@link CacheConfig}. Validates that the
 * cache manager bean is correctly
 * initialized and configured with the expected cache(s).
 */
@SpringBootTest(classes = CacheConfig.class)
class CacheConfigTest {

  /** Injected cache manager used for cache configuration validation. */
  @Autowired private CacheManager cacheManager;

  /** Default constructor. */
  public CacheConfigTest() {
    // No initialization required
  }

  /** Test that the cache manager bean is correctly injected and is not null. */
  @Test
  void cacheManagerIsNotNull() {
    assertNotNull(cacheManager, "CacheManager should not be null");
  }

  /** Test that the injected cache manager is an instance of CaffeineCacheManager. */
  @Test
  void cacheManagerIsCaffeineCacheManager() {
    assertInstanceOf(
        CaffeineCacheManager.class,
        cacheManager,
        "CacheManager should be an instance of CaffeineCacheManager");
  }

  /** Test that the cache manager contains a cache named 'emailValidationCache'. */
  @Test
  void cacheManagerContainsEmailValidationCache() {
    final CaffeineCacheManager manager = (CaffeineCacheManager) cacheManager;
    assertNotNull(
        manager.getCache("emailValidationCache"),
        "emailValidationCache should be present in CacheManager");
  }
}
