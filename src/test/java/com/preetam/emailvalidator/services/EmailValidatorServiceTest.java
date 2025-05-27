package com.preetam.emailvalidator.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.preetam.emailvalidator.models.EmailResponse;
import com.preetam.emailvalidator.models.MxResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class EmailValidatorServiceTest {

  @Mock private Logger mockLogger;

  @Mock private DnsLookupService dnsLookupService;

  @Mock private EmailValidatorService service;

  @Mock private GoogleSafeBrowsingService googleSafeBrowsingService;

  @BeforeEach
  void setup() {
    service = Mockito.spy(new EmailValidatorService(dnsLookupService, googleSafeBrowsingService));
  }

  @Test
  void testValidateEmailFullFlowSuccess() throws TextParseException {
    String email = "user@example.com";

    MXRecord mxRecord = mock(MXRecord.class);
    when(mxRecord.getTarget()).thenReturn(Name.fromString("mx1.example.com."));
    when(mxRecord.getPriority()).thenReturn(10);

    Record[] records = new Record[] {mxRecord};
    when(dnsLookupService.lookupMxRecords("example.com")).thenReturn(records);

    EmailResponse response = service.validateEmail(email);

    assertThat(response.getEmail()).isEqualTo(email);
    assertThat(response.getValidSyntax()).isTrue();
    assertThat(response.getMxResponse()).isNotNull();
    assertThat(response.getMxResponse().getHasMx()).isTrue();
    assertThat(response.getIsDisposable()).isFalse();
    assertThat(response.getMxResponse().getMxFailureReason()).isNull();
  }

  @Test
  void testValidateEmailInvalidSyntax() {
    String email = "invalid-email";

    EmailResponse response = service.validateEmail(email);

    assertThat(response.getEmail()).isEqualTo(email);
    assertThat(response.getValidSyntax()).isFalse();
    assertThat(response.getMxResponse()).isNotNull();
    assertThat(response.getMxResponse().getHasMx()).isFalse();
    assertThat(response.getMxResponse().getMxFailureReason()).isNotNull();
  }

  @Test
  void testValidateEmailDisposableDomain() throws TextParseException {
    String email = "user2@mailinator.com";

    MXRecord mxRecord = mock(MXRecord.class);
    when(mxRecord.getTarget()).thenReturn(Name.fromString("mx.mailinator.com."));
    when(mxRecord.getPriority()).thenReturn(5);

    Record[] records = new Record[] {mxRecord};
    when(dnsLookupService.lookupMxRecords("mailinator.com")).thenReturn(records);

    EmailResponse response = service.validateEmail(email);

    assertThat(response.getEmail()).isEqualTo(email);
    assertThat(response.getValidSyntax()).isTrue();
    assertThat(response.getMxResponse()).isNotNull();
    assertThat(response.getMxResponse().getHasMx()).isTrue();
    assertThat(response.getIsDisposable()).isTrue();
  }

  @Test
  void tesHasMxNoRecords() throws TextParseException {
    String email = "user@nodomain.com";

    when(dnsLookupService.lookupMxRecords("nodomain.com")).thenReturn(null);

    MxResponse mxResponse = service.mxRecord(email, "nodomain.com");

    assertThat(mxResponse.getHasMx()).isFalse();
    assertThat(mxResponse.getMxFailureReason())
        .isEqualTo("No MX records found for domain: nodomain.com");
  }

  @Test
  void testHasMxDomainExtractionFailure() {
    String email = "user@";

    MxResponse mxResponse = service.mxRecord(email, null);

    assertThat(mxResponse.getHasMx()).isFalse();
    assertThat(mxResponse.getMxFailureReason()).isEqualTo("Failure in extracting domain");
  }

  @Test
  void testHasMxTextParseException() throws TextParseException {
    String email = "user@baddomain";

    when(dnsLookupService.lookupMxRecords("baddomain"))
        .thenThrow(new TextParseException("bad domain"));

    MxResponse mxResponse = service.mxRecord(email, "baddomain");

    assertThat(mxResponse.getHasMx()).isFalse();
    assertThat(mxResponse.getMxFailureReason()).isEqualTo("Error parsing domain: baddomain");
  }

  @Test
  void testHasMxRecordsWithNoMxRecordInstances() throws TextParseException {
    String email = "user@example.com";
    String domain = "example.com";

    Record fakeRecord = mock(Record.class);
    Record[] records = new Record[] {fakeRecord};

    when(dnsLookupService.lookupMxRecords(domain)).thenReturn(records);

    var mxResponse = service.validateEmail(email).getMxResponse();

    assertThat(mxResponse.getHasMx()).isTrue();
  }

  @Test
  void testExtractDomainNullReturnedNoAtSymbol() {
    String email = "userexample.com"; // No '@'
    String domain = service.extractDomain(email);
    assertThat(domain).isNull();
  }

  @Test
  void testExtractDomainNullReturnedEndsWithAt() {
    String email = "user@"; // Ends with '@'
    String domain = service.extractDomain(email);
    assertThat(domain).isNull();
  }

  @Test
  void testValidateEmailEndsWithAt() {
    String email = "user@"; // Invalid, ends with @

    EmailResponse response = service.validateEmail(email);

    assertThat(response.getValidSyntax()).isFalse();
    assertThat(response.getMxResponse().getHasMx()).isFalse();
    assertThat(response.getMxResponse().getMxFailureReason())
        .isEqualTo("Failure in extracting domain");
  }

  @AfterEach
  void teardown() {}
}
