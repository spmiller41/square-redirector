package com.powersolutions.squarebridge.entities;

import com.powersolutions.squarebridge.square.dto.SquareCheckoutResponse;
import com.powersolutions.squarebridge.zoho.dto.ZohoInvoiceResponse;
import jakarta.persistence.*;

@Entity
@Table(name = "customer_checkout")
public class CustomerCheckout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "invoice_id")
    private String invoiceId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "balance")
    private double balance;

    @Column(name = "square_checkout_link")
    private String squareCheckoutLink;

    @Column(name = "square_order_id")
    private String squareOrderId;

    public CustomerCheckout() {}

    public CustomerCheckout(ZohoInvoiceResponse invoice, SquareCheckoutResponse checkoutResponse) {
        setInvoiceId(invoice.getInvoiceId());
        setCustomerId(invoice.getCustomerId());
        setFirstName(invoice.getFirstName());
        setLastName(invoice.getLastName());
        setPhone(invoice.getPhone());
        setEmail(invoice.getEmail());
        setBalance(invoice.getBalance());
        setSquareCheckoutLink(checkoutResponse.getCheckoutLink());
        setSquareOrderId(checkoutResponse.getOrderId());
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getInvoiceId() { return invoiceId; }

    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public double getBalance() { return balance; }

    public void setBalance(double balance) { this.balance = balance; }

    public String getSquareCheckoutLink() { return squareCheckoutLink; }

    public void setSquareCheckoutLink(String squareCheckoutLink) { this.squareCheckoutLink = squareCheckoutLink; }

    public String getSquareOrderId() { return squareOrderId; }

    public void setSquareOrderId(String squareOrderId) { this.squareOrderId = squareOrderId; }

    public String getCustomerId() { return customerId; }

    public void setCustomerId(String customerId) { this.customerId = customerId; }

    @Override
    public String toString() {
        return "CustomerCheckout{" +
                "id=" + id +
                ", invoiceId='" + invoiceId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                ", squareCheckoutLink='" + squareCheckoutLink + '\'' +
                ", squareOrderId='" + squareOrderId + '\'' +
                '}';
    }

}
