package com.powersolutions.squarebridge.entities;

import com.powersolutions.squarebridge.square.dto.SquarePaymentUpdate;
import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "status")
    private String status;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "amount")
    private double amount;

    // Foreign Key
    @Column(name = "customer_checkout_id")
    private int customerCheckoutId;

    public Payment() {}

    public Payment(SquarePaymentUpdate paymentUpdate, CustomerCheckout customerCheckout) {
        this.paymentId = paymentUpdate.getPaymentId();
        this.status = paymentUpdate.getStatus();
        this.receiptUrl = paymentUpdate.getReceiptUrl();
        this.amount = paymentUpdate.getAmount() / 100.0;
        this.customerCheckoutId = customerCheckout.getId();
    }

    public int getId() { return id; }
    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public String getReceiptUrl() { return receiptUrl; }
    public double getAmount() { return amount; }
    public int getCustomerCheckoutId() { return customerCheckoutId; }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentId='" + paymentId + '\'' +
                ", status='" + status + '\'' +
                ", receiptUrl='" + receiptUrl + '\'' +
                ", amount=" + amount +
                ", customerCheckoutId=" + customerCheckoutId +
                '}';
    }

}
