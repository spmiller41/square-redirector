package com.powersolutions.squarebridge.repo;

import com.powersolutions.squarebridge.entities.CustomerCheckout;
import com.powersolutions.squarebridge.entities.Payment;
import com.powersolutions.squarebridge.square.dto.SquarePaymentUpdate;
import com.powersolutions.squarebridge.zoho.dto.ZohoInvoiceResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomerCheckoutRepo {

    private final EntityManager entityManager;

    @Autowired
    public CustomerCheckoutRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<CustomerCheckout> findByPaymentRecord(Payment payment) {
        String query = "SELECT e FROM CustomerCheckout e WHERE e.id = :customerCheckoutId";

        try {
            CustomerCheckout customerCheckout = entityManager.createQuery(query, CustomerCheckout.class)
                    .setParameter("customerCheckoutId", payment.getCustomerCheckoutId())
                    .getSingleResult();

            return Optional.of(customerCheckout);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<CustomerCheckout> findByInvoiceId(String invoiceId) {
        String query = "SELECT e FROM CustomerCheckout e WHERE invoiceId = :invoiceId";

        try {
            CustomerCheckout customerCheckout = entityManager.createQuery(query, CustomerCheckout.class)
                    .setParameter("invoiceId", invoiceId)
                    .getSingleResult();
            return Optional.of(customerCheckout);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<CustomerCheckout> findByOrderId(SquarePaymentUpdate paymentData) {
        String query = "SELECT e FROM CustomerCheckout e WHERE e.squareOrderId = :squareOrderId";

        try {
            CustomerCheckout customerCheckout = entityManager.createQuery(query, CustomerCheckout.class)
                    .setParameter("squareOrderId", paymentData.getOrderId())
                    .getSingleResult();

            return Optional.of(customerCheckout);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<CustomerCheckout> safeInsert(CustomerCheckout customerCheckout) {
        try {
            entityManager.persist(customerCheckout);
            entityManager.flush();
            return Optional.of(customerCheckout);
        } catch (PersistenceException ex) {
            return Optional.empty();
        }
    }

}
