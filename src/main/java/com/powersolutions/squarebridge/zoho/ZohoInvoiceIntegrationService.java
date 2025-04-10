package com.powersolutions.squarebridge.zoho;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ZohoInvoiceIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(ZohoInvoiceIntegrationService.class);

    @Value("${zoho.invoice.api.endpoint}")
    private String baseUrl;

    @Value("${zoho.invoice.organization.id}")
    private String organizationId;

    private final ZohoInvoiceTokenService tokenService;
    private final RestTemplate restTemplate;

    public ZohoInvoiceIntegrationService(ZohoInvoiceTokenService tokenService, RestTemplate restTemplate) {
        this.tokenService = tokenService;
        this.restTemplate = restTemplate;
    }

    public Optional<String> getZohoInvoiceById(String invoiceId) {
        String accessToken = tokenService.getAccessToken("get");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Zoho-oauthtoken " + accessToken);
        headers.set("X-com-zoho-invoice-organizationid", organizationId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(baseUrl + invoiceId, HttpMethod.GET, httpEntity, String.class);

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

}
