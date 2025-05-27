package com.preetam.emailvalidator.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Represents a request containing an email address to be validated. */
@Data
public class EmailRequest {

  /** The email address to validate, must be a valid non-blank email. */
  @NotBlank @Email private String email;
}
