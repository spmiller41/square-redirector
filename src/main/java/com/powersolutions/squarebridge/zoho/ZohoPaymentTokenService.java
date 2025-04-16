package com.powersolutions.squarebridge.zoho;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.powersolutions.squarebridge.zoho.dto.ZohoTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZohoPaymentTokenService {

    private static final Logger logger = LoggerFactory.getLogger(ZohoPaymentTokenService.class);

    @Value("${zoho.invoice.token.base.url}")
    private String baseUrl;

    @Value("${zoho.invoice.payment.refresh.post}")
    private String paymentRefreshToken;

    @Value("${zoho.invoice.token.client.id}")
    private String clientId;

    @Value("${zoho.invoice.token.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, TokenDetails> tokenStore = new HashMap<>();

    public ZohoPaymentTokenService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getAccessToken() {
        TokenDetails tokenDetails = tokenStore.get("payment");

        if (tokenDetails == null || tokenDetails.isExpired()) {
            refreshToken();
        }

        return tokenStore.get("payment").accessToken();
    }

    private void refreshToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("refresh_token", paymentRefreshToken);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                ZohoTokenResponse tokenResponse = objectMapper.readValue(response.getBody(), ZohoTokenResponse.class);
                tokenStore.put("payment", new TokenDetails(tokenResponse.getAccessToken(), Instant.now(), tokenResponse.getExpiresIn()));
                logger.info("Refreshed Zoho Payment token.");
            } else {
                logger.error("Failed to refresh Zoho Payment token. Status: {}", response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.error("Exception during Zoho Payment token refresh.", ex);
        }
    }

    private record TokenDetails(String accessToken, Instant lastRefreshed, int expiresIn) {
        public boolean isExpired() {
            return Instant.now().getEpochSecond() - lastRefreshed.getEpochSecond() >= expiresIn;
        }
    }

}
