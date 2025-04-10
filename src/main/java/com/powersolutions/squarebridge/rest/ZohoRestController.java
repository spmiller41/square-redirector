package com.powersolutions.squarebridge.rest;

import com.powersolutions.squarebridge.zoho.ZohoInvoiceIntegrationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/v1/api")
public class ZohoRestController {

    @Autowired
    private ZohoInvoiceIntegrationService integrationService;

    @GetMapping("/create_payment")
    public void createPayment(@RequestParam("invoice_id") String invoiceId) {
        System.out.println("Invoice Id: " + invoiceId);
        Optional<String> optResponse = integrationService.getZohoInvoiceById(invoiceId);
        optResponse.ifPresent(System.out::println);
    }

}
