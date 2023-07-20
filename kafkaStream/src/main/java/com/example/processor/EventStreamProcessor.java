package com.example.processor;

import com.example.domain.Client;
import com.example.domain.FraudClient;
import com.example.domain.Transaction;
import com.example.serdes.CustomSerdes;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
public class EventStreamProcessor {
    @Autowired
    private StreamsBuilder streamsBuilder;

    @Value("${topic.create-client}")
    private String topicCreateClient;
    @Value("${topic.create-transaction}")
    private String topicCreateTransaction;

    @PostConstruct
    public void streamTopology() {

        // Create a KStream for the first topic (transactions)
        KStream<String, Transaction> transactionStream = streamsBuilder.stream(topicCreateTransaction,
                Consumed.with(Serdes.String(), CustomSerdes.transaction()));

        // Create a KTable for the second topic (clients)
        KTable<String, Client> clientTable = streamsBuilder.table(topicCreateClient,
                Consumed.with(Serdes.String(), CustomSerdes.client()));

        // Perform JOIN operation between KStream and KTable
        KStream<String, FraudClient> joinedStream = transactionStream.join(clientTable,
                (transaction, client) -> new FraudClient(client.getId(), client.getClientName(), transaction.getPrice() * transaction.getQuantity()),
                Joined.with(Serdes.String(), CustomSerdes.transaction(), CustomSerdes.client())
        );
        // Aggregate the sum of transactions for each client with a sliding window of two minutes
        KTable<Windowed<Long>, FraudClient> aggregationTable = joinedStream
                .filter((key, fraudClient) -> fraudClient.getClientName().length() > 8)
                .groupBy((key, fraudClient) -> fraudClient.getId(),
                        Grouped.with(Serdes.Long(), CustomSerdes.fraudClient()))
                .windowedBy(TimeWindows.of(Duration.ofMinutes(2)))
                .aggregate(
                        () -> new FraudClient(0L, "", 0.0),
                        (key, fraudClient, commonClientTotal) -> {
                            commonClientTotal.setId(fraudClient.getId());
                            commonClientTotal.setClientName(fraudClient.getClientName());
                            commonClientTotal.setTotal(commonClientTotal.getTotal() + fraudClient.getTotal());
                            return commonClientTotal;
                        },
                        Materialized.<Long, FraudClient, WindowStore<Bytes, byte[]>>as("fraud-client-aggregation-store")
                                .withKeySerde(Serdes.Long())
                                .withValueSerde(CustomSerdes.fraudClient())
                );
        // Write the filtered aggregation result to an output topic
        aggregationTable.toStream()
                .filter((windowedKey, fraudClient) -> fraudClient.getTotal() > 1000)
                .to("topicFraudClientTransaction",
                        Produced.with(WindowedSerdes.timeWindowedSerdeFrom(Long.class), CustomSerdes.fraudClient()));
    }
}
