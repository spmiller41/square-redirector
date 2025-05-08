package com.powersolutions.squarebridge.zoho.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ZohoInvoiceResponse {

    private final String invoiceId;
    private final String invoiceNumber;
    private final String customerId;
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
            this.invoiceNumber = invoice.path("invoice_number").asText("");
            this.balance = invoice.path("balance").asDouble();
            this.customerId = invoice.path("customer_id").asText("");

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

    /*
     * Normalizes a US phone number string into E.164 format.
     * <ul>
     *   <li>Returns null if the input is null, blank, or cannot be parsed to a 10-digit number.</li>
     *   <li>Strips all non-digit characters.</li>
     *   <li>If it’s 11 digits starting with '1', the leading '1' is removed.</li>
     *   <li>Returns "+1" followed by the 10-digit number if valid.</li>
     * </ul>
     *
     * @param rawPhone the raw phone string (may include punctuation, spaces, or country code)
     * @return the normalized E.164 phone number (e.g. "+13475677741") or null if invalid
     */
    private String normalizePhone(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank()) return null;
        String digits = rawPhone.replaceAll("\\D", "");

        if (digits.length() == 11 && digits.startsWith("1")) {
            digits = digits.substring(1);
        }

        if (digits.length() == 10) {
            return "+1" + digits;
        }
        return null;
    }


    public String getInvoiceId() { return invoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public double getBalance() { return balance; }

    @Override
    public String toString() {
        return "ZohoInvoiceResponse{" +
                "invoiceId='" + invoiceId + '\'' +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", balance=" + balance +
                '}';
    }

}
