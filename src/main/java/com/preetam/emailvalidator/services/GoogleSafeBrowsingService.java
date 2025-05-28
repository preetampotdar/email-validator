/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.services;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/** Service to check if a given domain is safe using the Google Safe Browsing API. */
@Service
public class GoogleSafeBrowsingService {

  /** Logger for logging API interaction details. */
  public static final Logger LOGGER = LoggerFactory.getLogger(GoogleSafeBrowsingService.class);

  /** Base URL template for the Google Safe Browsing API. */
  private static final String API_URL =
      "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=%s";

  /** The {@link RestTemplate} used to make HTTP requests. */
  private final RestTemplate restTemplate;

  /** The Google Safe Browsing API key, injected from application properties. */
  @Value("${google.safebrowsing.api.key}")
  public String apiKey;

  /**
   * Constructs a GoogleSafeBrowsingService with the given RestTemplate.
   *
   * @param restTemplate REST client used for API calls
   */
  public GoogleSafeBrowsingService(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Checks whether a given domain is considered safe by Google Safe Browsing.
   *
   * @param domain the domain name to check
   * @return {@code true} if the domain is safe; {@code false} otherwise
   */
  public boolean isDomainSafe(final String domain) {
    final String url = String.format(API_URL, apiKey);

    final Map<String, Object> requestBody =
        Map.of(
            "client",
                Map.of(
                    "clientId", "email-validator",
                    "clientVersion", "1.0"),
            "threatInfo",
                Map.of(
                    "threatTypes",
                        List.of(
                            "MALWARE",
                            "SOCIAL_ENGINEERING",
                            "UNWANTED_SOFTWARE",
                            "POTENTIALLY_HARMFUL_APPLICATION"),
                    "platformTypes", List.of("ANY_PLATFORM"),
                    "threatEntryTypes", List.of("URL"),
                    "threatEntries", List.of(Map.of("url", "http://" + domain))));

    boolean isSafe;
    try {

      @SuppressWarnings("PMD.LooseCoupling")
      final HttpHeaders headers = new HttpHeaders();

      headers.setContentType(MediaType.APPLICATION_JSON);

      final HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

      final ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

      final Map<?, ?> body = response.getBody();
      isSafe = (body == null || body.isEmpty());
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      if (getLogger().isWarnEnabled()) {
        getLogger().warn("Safe Browsing API returned error", ex);
      }
      isSafe = false;
    }

    return isSafe;
  }

  /**
   *
   * @return LOGGER instance
   */
  protected Logger getLogger() {
    return LOGGER;
  }
}
