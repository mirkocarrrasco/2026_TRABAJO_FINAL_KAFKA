package com.example.paymentstream.stream;

import com.example.paymentstream.model.Payment;
import com.example.paymentstream.serde.BigDecimalSerde;
import com.example.paymentstream.serde.PaymentSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentStream {

    private static final String INPUT_TOPIC = "payments";
    
    private static final String STATE_STORE = "payment-balances";

    @Bean
    public KTable<String, BigDecimal> processPayments(
            StreamsBuilder builder) {

        // 1. Leer el tópico payments
        KStream<String, Payment> paymentStream =
                builder.stream(
                        INPUT_TOPIC,
                        Consumed.with(
                                Serdes.String(),
                                new PaymentSerde()
                        )
                );

        // 2. Agrupar por card_id
        return paymentStream
                .groupByKey(
                        Grouped.with(
                                Serdes.String(),
                                new PaymentSerde()
                        )
                )

                // 3. Calcular el saldo
                .aggregate(
                        () -> BigDecimal.ZERO,

                        (cardId, payment, total) -> {

                            if ("A".equals(payment.type())) {
                                return total.add(payment.amount());
                            }

                            if ("C".equals(payment.type())) {
                                return total.subtract(payment.amount());
                            }

                            return total;
                        },
                        
                        Materialized
                        .<String, BigDecimal, KeyValueStore<Bytes, byte[]>>as(STATE_STORE)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(new BigDecimalSerde())
                );
    }
}