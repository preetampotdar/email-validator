/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.models;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.cache.test.autoconfigure.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for the {@link MxResponse} model.
 * This class tests the fields and JSON serialization
 * behavior of the model.
 */
@SpringBootTest
@AutoConfigureCache
@SuppressWarnings("PMD.AtLeastOneConstructor")
class MxResponseTest {

  /** JsonMapper for serializing/deserializing JSON. */
  @Autowired
  private JsonMapper jsonMapper;

  /** Verifies that 'hasMx' is correctly set and retrieved. */
  @Test
  void testHasMxField() {
    final MxResponse response = new MxResponse();
    response.setHasMx(true);

    assertThat(response.getHasMx()).isTrue();
  }

  /** Verifies that 'mxFailureReason' is correctly set and retrieved. */
  @Test
  void testMxFailureReasonField() {
    final MxResponse response = new MxResponse();
    response.setMxFailureReason("No MX record found");

    assertThat(response.getMxFailureReason()).isEqualTo("No MX record found");
  }

  /** Tests JSON serialization includes 'hasMx' when set. */
  @Test
  void testJsonIncludesHasMx() throws JsonProcessingException {
    final MxResponse response = new MxResponse();
    response.setHasMx(true);

    final String json = jsonMapper.writeValueAsString(response);

    assertThat(json).contains("\"hasMx\":true");
  }

  /** Tests JSON serialization excludes 'mxFailureReason' when not set. */
  @Test
  void testJsonExcludesNullMxFailureReason() throws JsonProcessingException {
    final MxResponse response = new MxResponse();
    // mxFailureReason is intentionally not set

    final String json = jsonMapper.writeValueAsString(response);

    assertThat(json).doesNotContain("mxFailureReason");
  }
}
