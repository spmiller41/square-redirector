package com.powersolutions.squarebridge.zoho.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ZohoInvoiceResponse {

    private final String invoiceId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private final double balance;

    public ZohoInvoiceResponse(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode invoice = root.path("invoice");

            this.invoiceId = invoice.path("invoice_id").asText("");
            this.balance = invoice.path("balance").asDouble();

            JsonNode contact = invoice.path("contact_persons_details").get(0);
            if (contact != null) {
                this.firstName = contact.path("first_name").asText("");
                this.lastName = contact.path("last_name").asText("");
                this.email = contact.path("email").asText("");
                this.phone = normalizePhone(contact.path("phone").asText(""));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Zoho Invoice JSON", e);
        }
    }

    private String normalizePhone(String rawPhone) {
        String digits = rawPhone.replaceAll("\\D", "");
        if (digits.length() == 10) return "+1" + digits;
        if (digits.length() == 11 && digits.startsWith("1")) return "+" + digits;
        return null;
    }

    public String getInvoiceId() { return invoiceId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public double getBalance() { return balance; }

    @Override
    public String toString() {
        return "ZohoInvoiceResponse{" +
                "invoiceId='" + invoiceId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", balance=" + balance +
                '}';
    }
}
