/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.health;

import com.preetam.emailvalidator.services.LookupFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/** Health indicator that checks DNS MX records for
 *  a known domain to verify DNS service health. */
@Component
public class DnsHealthIndicator implements HealthIndicator {

  /** Factory to create DNS lookup instances. */
  private final LookupFactory lookupFactory;

  /**
   * Constructs a DnsHealthIndicator with the given LookupFactory.
   *
   * @param lookupFactory factory to create DNS lookups
   */
  public DnsHealthIndicator(final LookupFactory lookupFactory) {
    this.lookupFactory = lookupFactory;
  }

  /**
   * Performs a health check by querying MX records for a known domain.
   *
   * @return Health status indicating up if MX records are found, down otherwise
   */
  @Override
  public Health health() {
    Health health;
    try {
      final Lookup lookup = lookupFactory.create("google.com", Type.MX);
      final Record[] records = lookup.run();

      if (records == null || records.length == 0) {
        health = Health.down().withDetail("reason", "No MX records found").build();
      } else {
        health = Health.up().withDetail("recordsFound", records.length).build();
      }
    } catch (TextParseException e) {
      health = Health.down(e).withDetail("error", "DNS lookup failed").build();
    }
    return health;
  }
}
