/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.configurations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for the {@link RateLimitingFilter} class to ensure correct behavior
 * under normal and rate-limited conditions.
 */
@SpringBootTest
@SuppressWarnings("PMD")
public class RateLimitingFilterTest {

  @Autowired private RateLimitingFilter filter;

  /**
   * Tests that the filter allows a request to proceed when the rate limit bucket has tokens.
   */
  @Test
  public void doFilterAllowsRequestWhenBucketHasTokens() throws IOException, ServletException {
    ServletRequest request = mock(ServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);

    when(request.getRemoteAddr()).thenReturn("127.0.0.1");

    filter.doFilter(request, response, chain);

    verify(chain, times(1)).doFilter(request, response);
    verify(response, never()).getWriter();
    verify(response, never()).setStatus(429);
  }

  /**
   * Tests that the filter blocks the request
   * and returns HTTP 429 when the rate limit bucket is empty.
   */
  @Test
  public void doFilterBlocksRequestWhenBucketIsEmpty() throws IOException, ServletException {
    String clientIp = "192.168.0.1";

    ServletRequest request = mock(ServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(request.getRemoteAddr()).thenReturn(clientIp);
    when(response.getWriter()).thenReturn(printWriter);

    var bucket = filter.resolveBucket(clientIp);
    for (int i = 0; i < 10; i++) {
      assertTrue(bucket.tryConsume(1));
    }

    FilterChain chain = mock(FilterChain.class);
    filter.doFilter(request, response, chain);

    verify(response, times(1)).setStatus(429);
    verify(response, times(1)).getWriter();
    verify(chain, never()).doFilter(request, response);

    printWriter.flush();
    assertEquals("Too many requests. Please try again later.", stringWriter.toString());
  }
}
