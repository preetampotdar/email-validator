package com.preetam.emailvalidator.configurations;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * A servlet filter that applies rate limiting using Bucket4j.
 * It ensures that a client identified by their IP address
 * cannot exceed a predefined request rate.
 */
@Component
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class RateLimitingFilter implements Filter {

  /** Cache for storing rate limit buckets per client IP address. */
  private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

  /**
   * Resolves or creates a rate limit bucket for the given client key.
   *
   * @param key the unique identifier for the client (e.g., IP address)
   * @return a Bucket instance managing rate limits for the key
   */
  public Bucket resolveBucket(final String key) {
    return bucketCache.computeIfAbsent(
        key,
        k ->
            Bucket.builder()
                .addLimit(
                    Bandwidth.builder()
                        .capacity(10)
                        .refillIntervally(10, Duration.ofMinutes(1))
                        .build())
                .build());
  }

  /** Filters requests with rate limiting; sends HTTP 429 when limit exceeded. */
  @Override
  @SuppressWarnings("PMD.LawOfDemeter")
  public void doFilter(
      final ServletRequest request, final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {

    final HttpServletResponse httpRes = (HttpServletResponse) response;

    final String clientIp = request.getRemoteAddr();
    final Bucket bucket = resolveBucket(clientIp);

    if (bucket.tryConsume(1)) {
      chain.doFilter(request, response);
    } else {
      httpRes.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      httpRes.getWriter().write("Too many requests. Please try again later.");
    }
  }
}
