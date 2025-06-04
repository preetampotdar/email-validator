/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.configurations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.preetam.emailvalidator.health.DnsHealthIndicator;
import com.preetam.emailvalidator.services.LookupFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/**
 * Unit tests for {@link DnsHealthIndicator}.
 * Tests DNS health check scenarios with mocked DNS
 * lookups.
 */
@SpringBootTest
@SuppressWarnings("PMD.AtLeastOneConstructor")
class DnsConfigTest {

  /** Mocked factory to create DNS Lookup instances. */
  @MockitoBean private LookupFactory lookupFactory;

  /** Constant for Google domain. */
  private static final String GOOGLE = "google.com";

  /** DNS health indicator to test DNS MX record health. */
  @Autowired private DnsHealthIndicator healthIndicator;

  /** When MX records are found, health status should be UP. */
  @Test
  @SuppressWarnings("PMD.LawOfDemeter")
  void healthWhenRecordsFoundShouldReturnUpStatus() throws Exception {
    final Lookup mockLookup = mock(Lookup.class);
    final Record[] records = {mock(MXRecord.class)};

    when(lookupFactory.create(GOOGLE, Type.MX)).thenReturn(mockLookup);
    when(mockLookup.run()).thenReturn(records);

    final Health health = healthIndicator.health();

    final Status status = health.getStatus();
    final String statusCode = status.getCode();
    assertThat(statusCode).isEqualTo("UP");
  }

  /** When MX records are found, health details should include record count. */
  @Test
  void healthWhenRecordsFoundShouldContainRecordCountInDetails() throws Exception {
    final Lookup mockLookup = mock(Lookup.class);
    final Record[] records = {mock(MXRecord.class)};

    when(lookupFactory.create(GOOGLE, Type.MX)).thenReturn(mockLookup);
    when(mockLookup.run()).thenReturn(records);

    final Health health = healthIndicator.health();

    assertThat(health.getDetails()).containsEntry("recordsFound", records.length);
  }

  /** When no MX records are found, health status should be DOWN. */
  @Test
  @SuppressWarnings("PMD.LawOfDemeter")
  void healthWhenNoRecordsShouldReturnDownStatus() throws Exception {
    final Lookup mockLookup = mock(Lookup.class);

    when(lookupFactory.create(GOOGLE, Type.MX)).thenReturn(mockLookup);
    when(mockLookup.run()).thenReturn(null);

    final Health health = healthIndicator.health();

    final Status status = health.getStatus();
    final String statusCode = status.getCode();
    assertThat(statusCode).isEqualTo("DOWN");
  }

  /** When no MX records are found, health details should include reason. */
  @Test
  void healthWhenNoRecordsShouldContainReasonInDetails() throws Exception {
    final Lookup mockLookup = mock(Lookup.class);

    when(lookupFactory.create(GOOGLE, Type.MX)).thenReturn(mockLookup);
    when(mockLookup.run()).thenReturn(null);

    final Health health = healthIndicator.health();

    assertThat(health.getDetails()).containsEntry("reason", "No MX records found");
  }

  /** When a DNS lookup exception occurs, health status should be DOWN. */
  @Test
  @SuppressWarnings("PMD.LawOfDemeter")
  void healthWhenTextParseExceptionThrownShouldReturnDownStatus() throws Exception {
    when(lookupFactory.create(GOOGLE, Type.MX)).thenThrow(new TextParseException("DNS failure"));

    final Health health = healthIndicator.health();

    final Status status = health.getStatus();
    final String statusCode = status.getCode();
    assertThat(statusCode).isEqualTo("DOWN");
  }

  /** When a DNS lookup exception occurs, health details should include error message. */
  @Test
  void healthWhenTextParseExceptionThrownShouldContainErrorInDetails() throws Exception {
    when(lookupFactory.create(GOOGLE, Type.MX)).thenThrow(new TextParseException("DNS failure"));

    final Health health = healthIndicator.health();

    assertThat(health.getDetails()).containsEntry("error", "DNS lookup failed");
  }
}
