package com.preetam.emailvalidator.services;

import org.springframework.stereotype.Service;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/**
 * Implementation of DnsLookupService that performs DNS MX record
 * lookups using the dnsjava library.
 */
@Service
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class DnsLookupServiceImpl implements DnsLookupService {
  @Override
  public Record[] lookupMxRecords(final String domain) throws TextParseException {
    return new Lookup(domain, Type.MX).run();
  }
}
