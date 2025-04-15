package com.powersolutions.squarebridge.service;

import com.powersolutions.squarebridge.entities.CustomerCheckout;
import com.powersolutions.squarebridge.entities.Payment;
import com.powersolutions.squarebridge.repo.CustomerCheckoutRepo;
import com.powersolutions.squarebridge.repo.PaymentRepo;
import com.powersolutions.squarebridge.square.dto.SquarePaymentUpdate;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service responsible for processing Square payment updates.
 * <p>
 * Verifies payment status and maps completed payments to existing
 * customer checkout records. If a payment has not been stored yet,
 * it will be inserted and linked to the associated checkout record.
 */
@Service
public class PaymentUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentUpdateService.class);

    // Defines the only status considered valid for persisting a payment
    private enum ValidStatus { COMPLETED }

    private final CustomerCheckoutRepo customerCheckoutRepo;
    private final PaymentRepo paymentRepo;


    @Autowired
    public PaymentUpdateService(CustomerCheckoutRepo customerCheckoutRepo, PaymentRepo paymentRepo) {
        this.customerCheckoutRepo = customerCheckoutRepo;
        this.paymentRepo = paymentRepo;
    }

    /**
     * Handles a Square payment update by validating the status and mapping it to an existing
     * customer checkout record. If no prior payment exists for the checkout, a new one is created.
     *
     * @param paymentData parsed payment update payload
     * @return the inserted Payment, or {@code null} if rejected or already processed
     */
    @Transactional
    public Payment handlePaymentUpdate(SquarePaymentUpdate paymentData) {
        if (!ValidStatus.COMPLETED.name().equalsIgnoreCase(paymentData.getStatus())) {
            logger.warn("Payment is not completed yet. Data: {}", paymentData);
            return null;
        }

        Optional<CustomerCheckout> optCustomerCheckout = customerCheckoutRepo.findByOrderId(paymentData);
        if (optCustomerCheckout.isEmpty()) {
            logger.error("Could not locate CustomerCheckout during payment update. Data: {}", paymentData);
            return null;
        }

        Optional<Payment> optPayment = paymentRepo.findByCustomerCheckoutId(optCustomerCheckout.get());
        if (optPayment.isEmpty()) {
            Payment payment = new Payment(paymentData, optCustomerCheckout.get());
            Optional<Payment> optNewPayment = paymentRepo.safeInsert(payment);
            if (optNewPayment.isEmpty()) {
                logger.error("Error occurred while attempting to persist payment. Data: {} -- {}", payment, optCustomerCheckout.get());
                return null;
            }
            logger.info("New payment inserted successfully. Data: {}", payment);
            return optNewPayment.get();
        }

        return null;
    }


}
