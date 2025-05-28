/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.controllers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.preetam.emailvalidator.models.EmailRequest;
import com.preetam.emailvalidator.models.EmailResponse;
import com.preetam.emailvalidator.models.MxResponse;
import com.preetam.emailvalidator.services.EmailValidatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**Unit tests for {@link EmailController}, focusing on the web layer.
 *
 * <p>
 * This test uses {@link WebMvcTest} to load only the {@code EmailController}
 * and related MVC components, excluding full application context.
 */
@WebMvcTest(EmailController.class)
@SuppressWarnings("PMD")
public class EmailControllerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired private MockMvc mockMvc;
  @MockitoBean private EmailValidatorService validatorService;

  @Test
  void validateEmailReturnsExpectedResponse() throws Exception {
    MxResponse mxResponse = new MxResponse();
    mxResponse.setHasMx(true);
    mxResponse.setMxFailureReason(null);

    EmailResponse mockResponse = new EmailResponse();
    mockResponse.setEmail("test@example.com");
    mockResponse.setValidSyntax(true);
    mockResponse.setMxResponse(mxResponse);
    mockResponse.setIsDisposable(false);

    when(validatorService.validateEmail(anyString())).thenReturn(mockResponse);

    EmailRequest request = new EmailRequest();
    request.setEmail("test@example.com");

    mockMvc
        .perform(
            post("/api/v1/email/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.validSyntax").value(true))
        .andExpect(jsonPath("$.mxResponse.hasMx").value(true))
        .andExpect(jsonPath("$.mxResponse.mxFailureReason").doesNotExist())
        .andExpect(jsonPath("$.isDisposable").value(false));
  }
}
