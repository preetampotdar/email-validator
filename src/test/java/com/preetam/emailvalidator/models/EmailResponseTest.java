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

/** Unit tests for {@link EmailResponse} to verify field setting
 *  and JSON serialization behavior. */
@SpringBootTest
@AutoConfigureCache
@SuppressWarnings("PMD.AtLeastOneConstructor")
class EmailResponseTest {

  /** JsonMapper for serializing/deserializing JSON. */
  @Autowired private JsonMapper jsonMapper;

  @Test
  void testEmailFieldIsSetCorrectly() {
    final EmailResponse response = new EmailResponse();
    response.setEmail("test@example.com");
    assertThat(response.getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void testValidSyntaxIsSetCorrectly() {
    final EmailResponse response = new EmailResponse();
    response.setValidSyntax(true);
    assertThat(response.getValidSyntax()).isTrue();
  }

  @Test
  void testIsDisposableIsSetCorrectly() {
    final EmailResponse response = new EmailResponse();
    response.setIsDisposable(true);
    assertThat(response.getIsDisposable()).isTrue();
  }

  @Test
  void testMxResponseIsSetCorrectly() {
    final MxResponse mxResponse = new MxResponse();
    mxResponse.setHasMx(true);
    mxResponse.setMxFailureReason("No MX records found");

    final EmailResponse response = new EmailResponse();
    response.setMxResponse(mxResponse);

    assertThat(response.getMxResponse()).isNotNull();
  }

  @Test
  @SuppressWarnings("PMD.LawOfDemeter")
  void testMxFailureReasonIsCorrect() {
    final MxResponse mxResponse = new MxResponse();
    mxResponse.setMxFailureReason("No MX records found");

    final EmailResponse response = new EmailResponse();
    response.setMxResponse(mxResponse);

    assertThat(response.getMxResponse().getMxFailureReason()).isEqualTo("No MX records found");
  }

  @Test
  void testJsonIncludesEmail() throws JsonProcessingException {
    final EmailResponse response = new EmailResponse();
    response.setEmail("abc@xyz.com");
    final String jsonOutput = jsonMapper.writeValueAsString(response);

    assertThat(jsonOutput).contains("\"email\":\"abc@xyz.com\"");
  }

  @Test
  void testJsonIncludesValidSyntax() throws JsonProcessingException {
    final EmailResponse response = new EmailResponse();
    response.setValidSyntax(true);
    final String jsonOutput = jsonMapper.writeValueAsString(response);

    assertThat(jsonOutput).contains("\"validSyntax\":true");
  }

  @Test
  void testJsonIncludesIsDisposable() throws JsonProcessingException {
    final EmailResponse response = new EmailResponse();
    response.setIsDisposable(false);
    final String jsonOutput = jsonMapper.writeValueAsString(response);

    assertThat(jsonOutput).contains("\"isDisposable\":false");
  }

  @Test
  void testJsonDoesNotIncludeMxIfNull() throws JsonProcessingException {
    final EmailResponse response = new EmailResponse();
    // mx is not set here
    final String jsonOutput = jsonMapper.writeValueAsString(response);

    assertThat(jsonOutput).doesNotContain("mx");
  }
}
