package com.preetam.emailvalidator.services;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.TextParseException;

/** Functional interface to create a DNS Lookup instance for
 *  a given domain and query type. */
@FunctionalInterface
public interface LookupFactory {

  /**
   * Creates a Lookup object for the specified domain and DNS record type.
   *
   * @param domain the domain name to query
   * @param type the DNS record type (e.g., MX, A)
   * @return a Lookup instance configured for the domain and type
   * @throws TextParseException if the domain name is invalid
   */
  Lookup create(String domain, int type) throws TextParseException;
}
