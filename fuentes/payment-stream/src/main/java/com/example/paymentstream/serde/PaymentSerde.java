package com.example.paymentstream.serde;

import com.example.paymentstream.model.Payment;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class PaymentSerde implements Serde<Payment> {

    private final JsonSerializer<Payment> serializer;
    private final JsonDeserializer<Payment> deserializer;

    public PaymentSerde() {

        this.serializer = new JsonSerializer<>();

        //this.deserializer = new JsonDeserializer<>(Payment.class);
        //this.deserializer.addTrustedPackages("*");
        
        this.deserializer = new JsonDeserializer<>(Payment.class, false);
        this.deserializer.addTrustedPackages("*");
    }

    @Override
    public Serializer<Payment> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<Payment> deserializer() {
        return deserializer;
    }
}