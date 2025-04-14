package com.powersolutions.squarebridge.rest;

import com.powersolutions.squarebridge.service.CustomerCheckoutService;
import com.powersolutions.squarebridge.square.SquareCheckoutIntegration;
import com.powersolutions.squarebridge.zoho.ZohoInvoiceIntegrationService;
import com.powersolutions.squarebridge.zoho.dto.ZohoInvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/v1/api")
public class ZohoRestController {

    @Autowired
    private CustomerCheckoutService checkoutService;

    @GetMapping("/create_payment")
    public ResponseEntity<Void> createPayment(@RequestParam("invoice_id") String invoiceId) {
        String checkoutLink = checkoutService.handleInvoiceCheckout(invoiceId);

        if (checkoutLink == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, checkoutLink)
                .build();
    }

}
