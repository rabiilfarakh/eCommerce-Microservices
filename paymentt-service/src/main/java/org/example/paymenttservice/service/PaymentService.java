package org.example.paymenttservice.service;

import org.example.paymenttservice.entity.Payment;
import org.springframework.stereotype.Service;


public interface PaymentService {
    Payment createPayment(Long orderId, Double amount);
    Payment getPaymentById(Long id);
}