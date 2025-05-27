package com.preetam.emailvalidator.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class GoogleSafeBrowsingServiceTest {

  @Mock private RestTemplate restTemplate;

  @Mock private Logger mockLogger;

  private GoogleSafeBrowsingService service;

  @BeforeEach
  void setup() {
    service = Mockito.spy(new GoogleSafeBrowsingService(restTemplate));
    service.apiKey = "dummy-api-key";
  }

  @Test
  void isDomainSafe_whenResponseBodyEmpty_shouldReturnTrue() {
    final ResponseEntity<Map> response = ResponseEntity.ok(Collections.emptyMap());
    when(restTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(response);

    final boolean result = service.isDomainSafe("example.com");

    assertThat(result).isTrue();
  }

  @Test
  void isDomainSafe_whenResponseBodyNotEmpty_shouldReturnFalse() {
    final Map<String, String> body = Map.of("threatType", "MALWARE");
    final ResponseEntity<Map> response = ResponseEntity.ok(body);
    when(restTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(response);

    final boolean result = service.isDomainSafe("example.com");

    assertThat(result).isFalse();
  }

  @Test
  void isDomainSafe_whenHttpClientErrorException_shouldLogWarning_andReturnFalse() {
    // Simulate logger enabled
    doReturn(mockLogger).when(service).getLogger();
    when(mockLogger.isWarnEnabled()).thenReturn(true);

    when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

    final boolean result = service.isDomainSafe("bad.com");

    verify(mockLogger).warn(anyString(), any(Throwable.class));
    assertThat(result).isFalse();
  }

  @Test
  void isDomainSafe_whenHttpClientErrorException_andLoggerDisabled_shouldNotLog_butReturnFalse() {
    // Simulate logger disabled
    doReturn(mockLogger).when(service).getLogger();
    when(mockLogger.isWarnEnabled()).thenReturn(false);

    when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

    final boolean result = service.isDomainSafe("bad.com");

    verify(mockLogger, never()).warn(anyString(), any(Throwable.class));
    assertThat(result).isFalse();
  }

  @Test
  void isDomainSafeReturnsTrueWhenResponseBodyIsNull() {
    when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

    boolean result = service.isDomainSafe("safedomain.com");

    assertThat(result).isTrue(); // because body == null
  }

  @Test
  void isDomainSafeReturnsTrueWhenResponseBodyIsEmpty() {
    when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(Collections.emptyMap(), HttpStatus.OK));

    boolean result = service.isDomainSafe("safedomain.com");

    assertThat(result).isTrue(); // because body.isEmpty() == true
  }

  @Test
  void isDomainSafeReturnsFalseWhenResponseBodyIsNotEmpty() {
    Map<String, Object> body = Map.of("threatMatches", List.of(Map.of("threatType", "MALWARE")));
    when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));

    boolean result = service.isDomainSafe("malicious.com");

    assertThat(result).isFalse(); // because body is not empty
  }

  @Test
  void getLoggerReturnsLoggerInstance() {
    Logger logger = service.getLogger();
    assertThat(logger).isNotNull();
    assertThat(logger).isSameAs(GoogleSafeBrowsingService.LOGGER);
  }
}
