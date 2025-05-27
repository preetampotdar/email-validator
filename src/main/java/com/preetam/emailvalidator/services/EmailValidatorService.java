package com.preetam.emailvalidator.services;

import com.preetam.emailvalidator.models.EmailResponse;
import com.preetam.emailvalidator.models.MxResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;

/**
 * This class provides email validation services including
 * syntax validation, MX record checks,
 * disposable email detection, and domain safety verification
 * via Google Safe Browsing API.
 */
@Service
public class EmailValidatorService {

  /** Logger for EmailValidatorService. */
  public static final Logger LOGGER = LoggerFactory.getLogger(EmailValidatorService.class);

  // can be replaced with
  // https://github.com/ZliIO/zliio-disposable?tab=readme-ov-file
  // https://github.com/disposable/disposable?tab=readme-ov-file
  /** List of known disposable email domains. */
  private static final List<String> DISP_DOMAINS =
      Arrays.asList("mailinator.com", "10minutemail.com", "guerrillamail.com", "yopmail.com");

  /** Service for DNS lookup operations. */
  private final DnsLookupService dnsLookupService;

  /** Service to check domain safety using Google Safe Browsing API. */
  private final GoogleSafeBrowsingService googleService;

  /**
   * Constructs an EmailValidatorService with the given dependencies.
   *
   * @param dnsLookupService service to perform DNS lookups
   * @param googleService service to check domain safety
   */
  public EmailValidatorService(
      final DnsLookupService dnsLookupService, final GoogleSafeBrowsingService googleService) {
    this.dnsLookupService = dnsLookupService;
    this.googleService = googleService;
  }

  /**
   * Validates an email address on multiple criteria.
   *
   * @param email the email to validate
   * @return response containing validation results
   */
  @Cacheable(value = "emailValidationCache", key = "#email")
  public EmailResponse validateEmail(final String email) {

    getLogger().info("Validating email: {}", email);
    final EmailResponse response = new EmailResponse();
    response.setEmail(email);

    final Boolean syntaxValid = isEmailSyntaxValid(email);
    getLogger().debug("Syntax valid: {}", syntaxValid);
    response.setValidSyntax(syntaxValid);

    final String domain = extractDomain(email);
    if (syntaxValid) {
      response.setIsSafe(googleService.isDomainSafe(domain));
    }

    final MxResponse mxRecord = mxRecord(email, domain);
    response.setMxResponse(mxRecord);

    final Boolean disposable = isDisposable(email);
    getLogger().debug("Is disposable: {}", disposable);
    response.setIsDisposable(disposable);

    return response;
  }

  /**
   * Checks if the given email has valid syntax.
   *
   * @param email email address to validate
   * @return true if syntax is valid, false otherwise
   */
  public Boolean isEmailSyntaxValid(final String email) {
    final EmailValidator validator = EmailValidator.getInstance(true, true);
    return validator.isValid(email);
  }

  /**
   * Checks MX records for the given email and domain.
   *
   * @param email the email address being checked
   * @param domain the domain extracted from the email
   * @return MxResponse with MX check results
   */
  public MxResponse mxRecord(final String email, final String domain) {
    getLogger().debug("Checking MX records for email: {}", email);

    final MxResponse mxResponse = new MxResponse();

    if (domain == null) {
      handleDomainNull(email, mxResponse);
    } else {

      try {
        final Record[] records = dnsLookupService.lookupMxRecords(domain);
        if (records == null) {
          handleNoRecords(domain, email, mxResponse);
        } else {
          logMxRecords(records, email);
          mxResponse.setHasMx(true);
        }
      } catch (TextParseException e) {
        handleParsingError(domain, email, mxResponse);
      }
    }
    return mxResponse;
  }

  private void handleDomainNull(final String email, final MxResponse mxResponse) {
    mxResponse.setHasMx(false);
    mxResponse.setMxFailureReason("Failure in extracting domain");
    getLogger().error("Failure in extracting domain for email: {}", email);
  }

  private void handleNoRecords(
      final String domain, final String email, final MxResponse mxResponse) {
    mxResponse.setHasMx(false);
    mxResponse.setMxFailureReason("No MX records found for domain: " + domain);
    getLogger().error("No MX records found for domain: {} for email: {}", domain, email);
  }

  private void handleParsingError(
      final String domain, final String email, final MxResponse mxResponse) {
    mxResponse.setHasMx(false);
    mxResponse.setMxFailureReason("Error parsing domain: " + domain);
    getLogger().error("Error parsing domain: {} for email: {}", domain, email);
  }

  @SuppressWarnings("PMD.GuardLogStatement")
  private void logMxRecords(final Record[] records, final String email) {
    for (final Record record : records) {
      if (record instanceof MXRecord mxRecord) {
        getLogger()
            .debug(
                "MX: {} | Priority: {} | email: {}",
                mxRecord.getTarget(),
                mxRecord.getPriority(),
                email);
      }
    }
  }

  /**Extracts the domain part from the given email address.
   *
   * @param email the email address to extract the domain from
   * @return the domain in lowercase if present;
   *        {@code null} if the email does not contain a valid domain
  */
  public String extractDomain(final String email) {
    final int atIndex = email.lastIndexOf('@');
    String domain = null;

    if (atIndex != -1 && atIndex < email.length() - 1) {
      domain = email.substring(atIndex + 1).toLowerCase(Locale.ROOT);
    }
    return domain;
  }

  /**
   * Checks if the email belongs to a disposable domain.
   *
   * @param email the email to check
   * @return true if email domain is disposable, false otherwise
   */
  public Boolean isDisposable(final String email) {
    final String domain = email.substring(email.indexOf('@') + 1);
    return DISP_DOMAINS.contains(domain);
  }

  protected Logger getLogger() {
    return LOGGER;
  }
}
