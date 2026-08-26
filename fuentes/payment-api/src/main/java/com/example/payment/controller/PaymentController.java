package com.example.payment.controller;

import com.example.payment.model.Payment;
import com.example.payment.producer.PaymentProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentProducer paymentProducer;

    public PaymentController(PaymentProducer paymentProducer) {
        this.paymentProducer = paymentProducer;
    }

    @PostMapping
    public ResponseEntity<Void> createPayment(
            @RequestBody Payment payment) {
    	System.out.println("**********************");
    	System.out.println("simba");
    	System.out.println("**********************");

        paymentProducer.send(payment);

        return ResponseEntity.ok().build();
    }
}
