package com.preetam.emailvalidator.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/** Unit tests for {@link LookupFactory} to verify Lookup creation behavior. */
@SpringBootTest
@SuppressWarnings("PMD.AtLeastOneConstructor")
class LookupFactoryTest {

  @Test
  void testCreateLookupSuccessfully() throws TextParseException {
    final LookupFactory factory = Lookup::new;
    final Lookup lookup = factory.create("example.com", Type.MX);

    assertThat(lookup).isNotNull();
  }

  @Test
  void testCreateLookupThrowsTextParseException() {
    final LookupFactory factory = Lookup::new;
    final String malformedDomain = "..invalid";

    assertThrows(TextParseException.class, () -> factory.create(malformedDomain, Type.MX));
  }
}
