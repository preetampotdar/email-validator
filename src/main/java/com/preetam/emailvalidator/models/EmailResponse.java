package com.preetam.emailvalidator.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Represents the response for email validation containing various
 * validation results and metadata.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailResponse {

  /** The email address being validated. */
  private String email;

  /** Indicates if the email syntax is valid. */
  private Boolean validSyntax;

  /** Indicates if the domain reputation is considered safe. */
  private Boolean isSafe;

  /** MX record validation results. */
  private MxResponse mxResponse;

  /** Indicates if the email belongs to a disposable domain. */
  private Boolean isDisposable;
}
