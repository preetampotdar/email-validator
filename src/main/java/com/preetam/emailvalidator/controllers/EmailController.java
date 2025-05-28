/*
 * Copyright 2025 Preetam Potdar
 */

package com.preetam.emailvalidator.controllers;

import com.preetam.emailvalidator.models.EmailRequest;
import com.preetam.emailvalidator.models.EmailResponse;
import com.preetam.emailvalidator.services.EmailValidatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller class for handling email validation requests. */
@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

  /** Service for email validation. */
  @Autowired private final EmailValidatorService validatorService;

  /**
   * Constructor for {@link EmailController}.
   *
   * @param validatorService the email validator service to be injected
   */
  public EmailController(final EmailValidatorService validatorService) {
    this.validatorService = validatorService;
  }

  /**
   * Validates an email address received in the request body.
   *
   * @param request the email validation request containing the email to validate
   * @return the validation result wrapped in {@link EmailResponse}
   */
  @PostMapping("/validate")
  public EmailResponse validateEmail(@Valid @RequestBody final EmailRequest request) {
    return validatorService.validateEmail(request.getEmail());
  }
}
