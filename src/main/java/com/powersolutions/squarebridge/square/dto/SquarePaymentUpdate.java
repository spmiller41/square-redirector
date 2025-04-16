package com.powersolutions.squarebridge.square.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DTO for parsing and holding data from Square's payment.updated webhook.
 * <p>
 * Extracts key payment fields: payment ID, status, order ID, and receipt URL.
 * This class is used to simplify handling of Square webhook payloads.
 */
public class SquarePaymentUpdate {

    private final String paymentId;
    private final String status;
    private final String orderId;
    private final String receiptUrl;
    private final int amount;

    /**
     * Constructs a SquarePaymentUpdate from a raw JSON string payload.
     * <p>
     * Parses the JSON to extract payment ID, status, order ID, and receipt URL.
     *
     * @param json the raw webhook payload from Square
     * @throws JsonProcessingException if the payload is malformed or unreadable
     */
    public SquarePaymentUpdate(String json) throws JsonProcessingException {
        JsonNode root = new ObjectMapper().readTree(json);
        JsonNode payment = root.path("data").path("object").path("payment");

        this.paymentId = payment.path("id").asText();
        this.status = payment.path("status").asText();
        this.orderId = payment.path("order_id").asText();
        this.receiptUrl = payment.path("receipt_url").asText();
        this.amount = payment.path("amount_money").path("amount").asInt();
    }

    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public String getOrderId() { return orderId; }
    public String getReceiptUrl() { return receiptUrl; }
    public int getAmount() { return amount; }

    @Override
    public String toString() {
        return "SquarePaymentUpdate{" +
                "paymentId='" + paymentId + '\'' +
                ", status='" + status + '\'' +
                ", orderId='" + orderId + '\'' +
                ", receiptUrl='" + receiptUrl + '\'' +
                ", amount=" + amount +
                '}';
    }

}
