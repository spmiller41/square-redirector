package com.powersolutions.squarebridge.square.dto;

public class SquareCheckoutResponse {

    private final String checkoutLink;
    private final String orderId;

    public SquareCheckoutResponse(String checkoutLink, String orderId) {
        this.checkoutLink = checkoutLink;
        this.orderId = orderId;
    }

    public String getCheckoutLink() { return checkoutLink; }

    public String getOrderId() { return orderId; }

    @Override
    public String toString() {
        return "SquareCheckoutResponse{" +
                "checkoutLink='" + checkoutLink + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }

}
