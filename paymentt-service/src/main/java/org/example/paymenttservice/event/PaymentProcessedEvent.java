package org.example.paymenttservice.event;

import lombok.Data;


public class PaymentProcessedEvent {
    private Long orderId;
    private String status;
    private String transactionId;

    public PaymentProcessedEvent() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentProcessedEvent(Long orderId, String transactionId, String status) {
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.status = status;
    }
}