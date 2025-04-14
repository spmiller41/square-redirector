package com.powersolutions.squarebridge.service;

import com.powersolutions.squarebridge.entities.CustomerCheckout;
import com.powersolutions.squarebridge.repo.CustomerCheckoutRepo;
import com.powersolutions.squarebridge.square.SquareCheckoutIntegration;
import com.powersolutions.squarebridge.square.dto.SquareCheckoutResponse;
import com.powersolutions.squarebridge.zoho.ZohoInvoiceIntegrationService;
import com.powersolutions.squarebridge.zoho.dto.ZohoInvoiceResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service responsible for managing customer checkout flows.
 * <p>
 * Handles the logic for retrieving or generating a Square payment link
 * tied to a Zoho invoice. If a checkout link already exists, it is reused;
 * otherwise, a new one is generated and persisted. This service acts as the
 * bridge between Zoho Invoice data and Square Checkout integration.
 */
@Service
public class CustomerCheckoutService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerCheckoutService.class);

    private final CustomerCheckoutRepo customerCheckoutRepo;
    private final ZohoInvoiceIntegrationService invoiceIntegrationService;
    private final SquareCheckoutIntegration squareCheckoutIntegration;

    @Autowired
    public CustomerCheckoutService(CustomerCheckoutRepo customerCheckoutRepo,
                                   ZohoInvoiceIntegrationService invoiceIntegrationService,
                                   SquareCheckoutIntegration squareCheckoutIntegration) {
        this.customerCheckoutRepo = customerCheckoutRepo;
        this.invoiceIntegrationService = invoiceIntegrationService;
        this.squareCheckoutIntegration = squareCheckoutIntegration;
    }

    /**
     * Retrieves or generates a Square checkout link for the given Zoho invoice ID.
     * <p>
     * If a checkout link already exists in the database, it returns the existing one.
     * Otherwise, it fetches the invoice details from Zoho, generates a new payment link
     * via Square, persists the new checkout record, and returns the link.
     *
     * @param invoiceId the unique Zoho invoice ID
     * @return the Square-hosted checkout link, or {@code null} if the invoice is not found
     */
    @Transactional
    public String handleInvoiceCheckout(String invoiceId) {
        Optional<CustomerCheckout> optCustomerCheckout = customerCheckoutRepo.findByInvoiceId(invoiceId);
        if (optCustomerCheckout.isPresent()) {
            logger.info("Existing checkout found for invoice: {}", invoiceId);
            return optCustomerCheckout.get().getSquareCheckoutLink();
        }

        Optional<String> optInvoice = invoiceIntegrationService.getZohoInvoiceById(invoiceId);
        if (optInvoice.isEmpty()) {
            logger.warn("Invoice not found for ID: {}", invoiceId);
            return null;
        }
        ZohoInvoiceResponse invoice = new ZohoInvoiceResponse(optInvoice.get());

        SquareCheckoutResponse checkoutResponse = squareCheckoutIntegration.createPaymentLink(invoice);
        insertCustomerCheckoutData(invoice, checkoutResponse);
        return checkoutResponse.getCheckoutLink();
    }

    private void insertCustomerCheckoutData(ZohoInvoiceResponse invoice, SquareCheckoutResponse checkoutResponse) {
        CustomerCheckout customerCheckout = new CustomerCheckout(invoice, checkoutResponse);
        Optional<CustomerCheckout> optCustomerCheckout = customerCheckoutRepo.safeInsert(customerCheckout);
        if (optCustomerCheckout.isPresent()) {
            logger.info("Customer Checkout Data Insert Successful. Data: {}", optCustomerCheckout.get());
        } else {
            logger.error("Customer Checkout Data Failed Insert. Invoice: {}", invoice);
        }
    }

}
