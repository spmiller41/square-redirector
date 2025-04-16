package com.powersolutions.squarebridge.zoho;

import com.powersolutions.squarebridge.entities.CustomerCheckout;
import com.powersolutions.squarebridge.entities.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ZohoInvoiceIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(ZohoInvoiceIntegrationService.class);

    @Value("${zoho.invoice.api.endpoint}")
    private String invoiceApiEndpoint;

    @Value("${zoho.invoice.payment.api.endpoint}")
    private String paymentApiEndpoint;

    @Value("${zoho.invoice.organization.id}")
    private String organizationId;

    private final ZohoInvoiceTokenService invoiceTokenService;
    private final ZohoPaymentTokenService paymentTokenService;
    private final RestTemplate restTemplate;

    public ZohoInvoiceIntegrationService(ZohoInvoiceTokenService invoiceTokenService,
                                         ZohoPaymentTokenService paymentTokenService,
                                         RestTemplate restTemplate) {
        this.invoiceTokenService = invoiceTokenService;
        this.paymentTokenService = paymentTokenService;
        this.restTemplate = restTemplate;
    }

    public Optional<String> getZohoInvoiceById(String invoiceId) {
        String accessToken = invoiceTokenService.getAccessToken("get");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Zoho-oauthtoken " + accessToken);
        headers.set("X-com-zoho-invoice-organizationid", organizationId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(invoiceApiEndpoint + invoiceId, HttpMethod.GET, httpEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Zoho Invoice Fetched Successfully: {}", response.getStatusCode());
                return Optional.ofNullable(response.getBody());
            } else if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
                logger.error("Failed to Retrieve Zoho Invoice: {}", response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.error("Exception occurred while attempting to fetch Zoho Invoice {}", ex.getMessage());
        }

        return Optional.empty();
    }

    public void createZohoPayment(CustomerCheckout checkout, Payment payment) {
        String accessToken = paymentTokenService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Zoho-oauthtoken " + accessToken);
        headers.set("X-com-zoho-invoice-organizationid", organizationId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build request body with required fields only
        Map<String, Object> body = new HashMap<>();
        body.put("customer_id", checkout.getCustomerId());
        body.put("payment_mode", "cash");
        body.put("amount", payment.getAmount());
        body.put("date", java.time.LocalDate.now().toString());
        body.put("reference_number", "SQUARE_PAY-" + payment.getPaymentId());

        Map<String, Object> invoice = new HashMap<>();
        invoice.put("invoice_id", checkout.getInvoiceId());
        invoice.put("amount_applied", checkout.getBalance());
        body.put("invoices", List.of(invoice));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    paymentApiEndpoint,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Zoho payment created successfully. Response: {}", response.getBody());
            } else {
                logger.error("Failed to create Zoho payment. Status: {}", response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.error("Exception occurred while creating Zoho payment: {}", ex.getMessage());
        }
    }


}
