package com.example.paymentstream.controller;

import com.example.paymentstream.model.CardBalance;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.StoreQueryParameters;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private static final String STATE_STORE = "payment-balances";

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    public PaymentController(
            StreamsBuilderFactoryBean streamsBuilderFactoryBean) {

        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
    }

    @GetMapping("/search")
    public List<CardBalance> search() {
    	
    	System.out.println("Simba esta dentro del metodo search:");

        KafkaStreams kafkaStreams =
                streamsBuilderFactoryBean.getKafkaStreams();

        ReadOnlyKeyValueStore<String, BigDecimal> store =
                kafkaStreams.store(
                        StoreQueryParameters.fromNameAndType(
                                STATE_STORE,
                                QueryableStoreTypes.keyValueStore()
                        )
                );

        List<CardBalance> result = new ArrayList<>();

        try (KeyValueIterator<String, BigDecimal> iterator =
                     store.all()) {

            while (iterator.hasNext()) {

                KeyValue<String, BigDecimal> entry =
                        iterator.next();

                result.add(
                        new CardBalance(
                                entry.key,
                                entry.value
                        )
                );
            }
        }

        return result;
    }
}