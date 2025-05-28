/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;

/**
 * Unit tests for {@link DnsLookupServiceImpl}.
 * Validates behavior of MX record lookups for existing
 * and non-existing domains.
 */
@SpringBootTest
@SuppressWarnings("PMD.AtLeastOneConstructor")
class DnsLookupServiceImplTest {

  /** The service under test, responsible for DNS MX record lookups. */
  @Autowired private DnsLookupServiceImpl dnsLookupService;

  /** Test that MX records are returned for a valid domain like gmail.com. */
  @Test
  void testMxRecordsNotNullForValidDomain() throws TextParseException {
    final Record[] records = dnsLookupService.lookupMxRecords("gmail.com");

    assertThat(records).isNotNull();
  }

  /** Test that MX records are non-empty for a valid domain like gmail.com. */
  @Test
  void testMxRecordsNotEmptyForValidDomain() throws TextParseException {
    final Record[] records = dnsLookupService.lookupMxRecords("gmail.com");

    assertThat(records).isNotEmpty();
  }

  /** Test that MX records are null for a non-existent domain. */
  @Test
  void testMxRecordsNullForNonExistentDomain() throws TextParseException {
    final Record[] records = dnsLookupService.lookupMxRecords("nonexistentdomainforsure12345.com");

    assertThat(records).isNull();
  }
}
