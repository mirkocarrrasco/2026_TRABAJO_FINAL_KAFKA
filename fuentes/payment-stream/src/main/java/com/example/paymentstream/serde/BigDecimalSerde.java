package com.example.paymentstream.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public class BigDecimalSerde implements Serde<BigDecimal> {

    @Override
    public Serializer<BigDecimal> serializer() {

        return (topic, data) -> {

            if (data == null) {
                return null;
            }

            return data.toPlainString()
                    .getBytes(StandardCharsets.UTF_8);
        };
    }

    @Override
    public Deserializer<BigDecimal> deserializer() {

        return (topic, data) -> {

            if (data == null) {
                return null;
            }

            return new BigDecimal(
                    new String(data, StandardCharsets.UTF_8)
            );
        };
    }
}
