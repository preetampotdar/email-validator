/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.preetam.emailvalidator.services.LookupFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/**
 * Unit tests for {@link DnsHealthIndicator},
 * which verifies DNS MX record availability for a given
 * domain.
 */
@SpringBootTest(classes = DnsHealthIndicator.class)
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.LawOfDemeter"})
class DnsHealthIndicatorTest {

  /** Google domain. */
  private static final String TEST_DOMAIN = "google.com";

  /** Mocked factory to provide DNS Lookup instances. */
  @MockitoBean private LookupFactory lookupFactory;

  /** System under test: the DNS health indicator. */
  @Autowired private DnsHealthIndicator indicator;

  @Test
  void healthReturnsUpWhenMxRecordsFound() throws Exception {
    final Lookup mockLookup = mock(Lookup.class);
    when(mockLookup.run()).thenReturn(new Record[] {mock(MXRecord.class)});
    when(lookupFactory.create(TEST_DOMAIN, Type.MX)).thenReturn(mockLookup);

    final Health health = indicator.health();
    final String status = health.getStatus().getCode();

    assertThat(status).isEqualTo("UP");
  }

  @Test
  void healthIncludesRecordCountWhenMxRecordsFound() throws Exception {
    final Lookup mockLookup = mock(Lookup.class);
    when(mockLookup.run()).thenReturn(new Record[] {mock(MXRecord.class)});
    when(lookupFactory.create(TEST_DOMAIN, Type.MX)).thenReturn(mockLookup);

    final Health health = indicator.health();

    assertThat(health.getDetails()).containsEntry("recordsFound", 1);
  }

  @Test
  void healthReturnsDownWhenNoMxRecords() throws Exception {
    final Lookup mockLookup = mock(Lookup.class);
    when(mockLookup.run()).thenReturn(null);
    when(lookupFactory.create(TEST_DOMAIN, Type.MX)).thenReturn(mockLookup);

    final Health result = indicator.health();
    final String status = result.getStatus().getCode();

    assertThat(status).isEqualTo("DOWN");
  }

  @Test
  void healthIncludesReasonWhenNoMxRecords() throws Exception {
    final Lookup mockLookup = mock(Lookup.class);
    when(mockLookup.run()).thenReturn(null);
    when(lookupFactory.create(TEST_DOMAIN, Type.MX)).thenReturn(mockLookup);

    final Health result = indicator.health();

    assertThat(result.getDetails()).containsEntry("reason", "No MX records found");
  }

  @Test
  void healthReturnsDownOnTextParseException() throws Exception {
    when(lookupFactory.create(TEST_DOMAIN, Type.MX)).thenThrow(new TextParseException("Invalid"));

    final Health result = indicator.health();
    final String status = result.getStatus().getCode();

    assertThat(status).isEqualTo("DOWN");
  }

  @Test
  void healthIncludesErrorDetailsOnTextParseException() throws Exception {
    when(lookupFactory.create(TEST_DOMAIN, Type.MX)).thenThrow(new TextParseException("Invalid"));

    final Health result = indicator.health();

    assertThat(result.getDetails()).containsEntry("error", "DNS lookup failed");
  }

  @Test
  void healthReturnsDownWhenRecordsEmpty() throws Exception {
    final Lookup mockLookup = mock(Lookup.class);
    final Record[] emptyRecords = new Record[0];

    when(lookupFactory.create(TEST_DOMAIN, Type.MX)).thenReturn(mockLookup);
    when(mockLookup.run()).thenReturn(emptyRecords);

    final Health health = indicator.health();
    final String status = health.getStatus().getCode();

    assertThat(status).isEqualTo("DOWN");
  }

  @Test
  void healthIncludesReasonWhenRecordsEmpty() throws Exception {
    final Lookup mockLookup = mock(Lookup.class);
    final Record[] emptyRecords = new Record[0];

    when(lookupFactory.create(TEST_DOMAIN, Type.MX)).thenReturn(mockLookup);
    when(mockLookup.run()).thenReturn(emptyRecords);

    final Health health = indicator.health();

    assertThat(health.getDetails()).containsEntry("reason", "No MX records found");
  }
}
