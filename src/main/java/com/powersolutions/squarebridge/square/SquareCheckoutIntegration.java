package com.powersolutions.squarebridge.square;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powersolutions.squarebridge.square.dto.SquareCheckoutResponse;
import com.powersolutions.squarebridge.zoho.dto.ZohoInvoiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SquareCheckoutIntegration {

    private static final Logger logger = LoggerFactory.getLogger(SquareCheckoutIntegration.class);

    @Value("${square.api.url}")
    private String squareApiUrl;

    @Value("${square.access.token}")
    private String accessToken;

    @Value("${square.location.id}")
    private String locationId;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    public SquareCheckoutIntegration(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    public SquareCheckoutResponse createPaymentLink(ZohoInvoiceResponse invoice) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("Square-Version", "2025-03-19");

            Map<String, Object> requestBody = payloadBody(invoice);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(squareApiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String url = root.path("payment_link").path("url").asText();
                String orderId = root.path("payment_link").path("order_id").asText();
                logger.info("Checkout link created. Order ID: {}, Invoice: {}", orderId, invoice.getInvoiceId());
                return new SquareCheckoutResponse(url, orderId);
            } else {
                logger.error("Failed to create Square payment link: {}", response.getBody());
                return null;
            }
        } catch (Exception ex) {
            logger.error("Exception creating Square payment link {}", ex.getMessage());
            return null;
        }
    }


    private Map<String, Object> payloadBody(ZohoInvoiceResponse invoice) {
        int amountWithoutDecimal = (int) Math.round(invoice.getBalance() * 100);

        Map<String, Object> priceMoney = new HashMap<>();
        priceMoney.put("amount", amountWithoutDecimal);
        priceMoney.put("currency", "USD");

        Map<String, Object> quickPay = new HashMap<>();
        quickPay.put("name", generateInvoiceName(invoice));
        quickPay.put("price_money", priceMoney);
        quickPay.put("location_id", locationId);

        Map<String, Object> prePopulatedData = new HashMap<>();
        prePopulatedData.put("buyer_email", invoice.getEmail());
        prePopulatedData.put("buyer_phone_number", invoice.getPhone());

        Map<String, Object> body = new HashMap<>();
        body.put("idempotency_key", UUID.randomUUID().toString());
        body.put("quick_pay", quickPay);
        body.put("pre_populated_data", prePopulatedData);

        return body;
    }


    private String generateInvoiceName(ZohoInvoiceResponse invoice) {
        String invoiceNumber = invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber().trim() : "";
        String firstName = invoice.getFirstName() != null ? invoice.getFirstName().trim() : "";
        String lastName = invoice.getLastName() != null ? invoice.getLastName().trim() : "";
        String fullName = (firstName + " " + lastName).trim();

        String base = "Power Solutions Invoice";
        String withInvoiceNumber = invoiceNumber.isEmpty() ? base : base + " (" + invoiceNumber + ")";

        return fullName.isEmpty()
                ? withInvoiceNumber
                : withInvoiceNumber + " for " + fullName;
    }


}
