/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.beans;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class for setting up cache using Caffeine. */
@Configuration
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CacheConfig {

  /**
   * Configures a Caffeine-backed CacheManager bean for email validation caching.
   *
   * @return a CacheManager instance with custom Caffeine settings
   */
  @Bean
  public CacheManager cacheManager() {
    final CaffeineCacheManager cacheManager = new CaffeineCacheManager("emailValidationCache");
    cacheManager.setCaffeine(
        Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .recordStats());
    return cacheManager;
  }
}
