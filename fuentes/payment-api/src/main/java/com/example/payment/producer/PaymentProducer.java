package com.example.payment.producer;

import com.example.payment.model.Payment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {

    private static final String TOPIC = "payments";

    private final KafkaTemplate<String, Payment> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, Payment> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Payment payment) {

        kafkaTemplate.send(
                TOPIC,
                payment.card_id(),
                payment
        );
    }
}
