package com.preetam.emailvalidator.services;

import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;

/** Interface defining DNS lookup operations. */
public interface DnsLookupService {

  /**
   * Looks up MX records for the specified domain.
   *
   * @param domain the domain name to query MX records for
   * @return array of DNS MX records
   * @throws TextParseException if the domain name is invalid
   */
  Record[] lookupMxRecords(String domain) throws TextParseException;
}
