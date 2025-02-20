package org.example.paymenttservice.listener;

import org.example.paymenttservice.event.OrderCreatedEvent;
import org.example.paymenttservice.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    private final PaymentService paymentService;

    public OrderCreatedListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        paymentService.createPayment(event.getOrderId(), event.getAmount());
    }
}