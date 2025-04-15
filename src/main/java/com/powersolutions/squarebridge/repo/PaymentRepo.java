package com.powersolutions.squarebridge.repo;

import com.powersolutions.squarebridge.entities.CustomerCheckout;
import com.powersolutions.squarebridge.entities.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PaymentRepo {

    private final EntityManager entityManager;

    @Autowired
    public PaymentRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Payment> findByCustomerCheckoutId(CustomerCheckout customerCheckout) {
        String query = "SELECT e FROM Payment e WHERE customerCheckoutId = :customerCheckoutId";

        try {
            Payment payment = entityManager.createQuery(query, Payment.class)
                    .setParameter("customerCheckoutId", customerCheckout.getId())
                    .getSingleResult();

            return Optional.of(payment);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<Payment> safeInsert(Payment payment) {
        try {
            entityManager.persist(payment);
            entityManager.flush();
            return Optional.of(payment);
        } catch (PersistenceException ex) {
            return Optional.empty();
        }
    }

}
