package com.preetam.emailvalidator.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/** Represents the result of an MX record lookup for a domain. */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MxResponse {

  /** Indicates whether the domain has valid MX records. */
  private Boolean hasMx;

  /** Description or reason if MX record lookup failed. */
  private String mxFailureReason;
}
