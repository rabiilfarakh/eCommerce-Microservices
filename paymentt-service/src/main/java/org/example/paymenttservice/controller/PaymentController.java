package org.example.paymenttservice.controller;

import org.example.paymenttservice.entity.Payment;
import org.example.paymenttservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.paymenttservice.dto.PaymentRequest;
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment createPayment(@RequestBody PaymentRequest request) {
        return paymentService.createPayment(request.getOrderId(), request.getAmount());
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }
}