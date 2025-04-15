package com.powersolutions.squarebridge.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.powersolutions.squarebridge.security.SquareSignatureVerifier;
import com.powersolutions.squarebridge.service.PaymentUpdateService;
import com.powersolutions.squarebridge.square.dto.SquarePaymentUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api")
public class SquareRestController {

    private static final Logger logger = LoggerFactory.getLogger(SquareRestController.class);

    private final SquareSignatureVerifier signatureVerifier;
    private final PaymentUpdateService paymentUpdateService;

    @Autowired
    public SquareRestController(SquareSignatureVerifier signatureVerifier, PaymentUpdateService paymentUpdateService) {
        this.signatureVerifier = signatureVerifier;
        this.paymentUpdateService = paymentUpdateService;
    }

    @PostMapping("/payment_updates")
    public ResponseEntity<Void> paymentUpdates(@RequestBody String paymentUpdatePayload,
                                               @RequestHeader("x-square-hmacsha256-signature") String signatureHeader) {

        if (!signatureVerifier.isValidSignature(paymentUpdatePayload, signatureHeader)) {
            logger.warn("Square webhook signature verification failed.");
            return ResponseEntity.status(401).build();
        }

        try {
            SquarePaymentUpdate paymentUpdate = new SquarePaymentUpdate(paymentUpdatePayload);
            logger.info("Received Square payment update: {}", paymentUpdate);
            paymentUpdateService.handlePaymentUpdate(paymentUpdate);
            return ResponseEntity.ok().build();
        } catch (JsonProcessingException ex) {
            logger.error("Error while attempting to parse Square payment update: {}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        }

    }

}
