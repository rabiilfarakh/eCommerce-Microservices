package org.example.paymenttservice.service.impl;

import org.example.paymenttservice.entity.Payment;
import org.example.paymenttservice.event.PaymentProcessedEvent;
import org.example.paymenttservice.repository.PaymentRepository;
import org.example.paymenttservice.service.PaymentService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    public PaymentServiceImpl(PaymentRepository paymentRepository, RabbitTemplate rabbitTemplate) {
        this.paymentRepository = paymentRepository;
        this.rabbitTemplate = rabbitTemplate;
    }


    private String generateTransactionId() {
        return "TX-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
    @Override

    public Payment createPayment(Long orderId, Double amount) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        payment.setPaymentMethod("CARTE");
        boolean paymentSuccess = simulatePaymentGateway();

        if (paymentSuccess) {
            payment.setStatus("SUCCESS");
            payment.setTransactionId(generateTransactionId());
        } else {
            payment.setStatus("FAILED");
        }

        Payment savedPayment = paymentRepository.save(payment);

        PaymentProcessedEvent event = new PaymentProcessedEvent();
        event.setOrderId(orderId);
        event.setStatus(payment.getStatus());
        event.setTransactionId(payment.getTransactionId());
        rabbitTemplate.convertAndSend("payment.processed.queue", event);

        return savedPayment;
    }


    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
    }

    private boolean simulatePaymentGateway() {
        return Math.random() > 0.5;
    }
}