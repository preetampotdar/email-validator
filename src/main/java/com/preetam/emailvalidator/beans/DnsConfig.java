package com.preetam.emailvalidator.beans;

import com.preetam.emailvalidator.services.LookupFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xbill.DNS.Lookup;

/** Configuration class for DNS-related beans used in email validation. */
@Configuration
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class DnsConfig {

  /**
   * Defines a bean for the LookupFactory, which supplies DNS lookup instances.
   *
   * @return a LookupFactory using default Lookup instantiation
   */
  @Bean
  public LookupFactory lookupFactory() {
    return Lookup::new;
  }
}
