package com.example.processor;

import com.example.domain.Client;
import com.example.domain.FraudClient;
import com.example.domain.Transaction;
import com.example.serdes.CustomSerdes;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Properties;


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
        Properties config = new Properties();
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass()); // Set the default key serde


        // Создаем KStream для первого топика (транзакции)
        KStream<String, Transaction> transactionStream = streamsBuilder.stream(topicCreateTransaction,
                Consumed.with(Serdes.String(), CustomSerdes.transaction()));

        // Создаем KTable для второго топика (клиенты)
        KTable<String, Client> clientTable = streamsBuilder.table(topicCreateClient,
                Consumed.with(Serdes.String(), CustomSerdes.client()));

        // Операция JOIN для KStream и KTable
        KStream<String, FraudClient> joinedStream = transactionStream.join(clientTable,
                (transaction, client) -> new FraudClient(client.getId(), client.getClientName(), transaction.getPrice()),
                Joined.with(Serdes.String(), CustomSerdes.transaction(), CustomSerdes.client())
        );

        // Агрегация суммы транзакций для каждого клиента со скользящим окном в две минуты
        KTable<Windowed<Long>, FraudClient> aggregationTable = joinedStream
                .filter((key, fraudClient) -> fraudClient.getClientName().length() > 8) // Filter by client name length of 8 characters
                .groupBy((key, fraudClient) -> fraudClient.getId())
                .windowedBy(TimeWindows.of(Duration.ofMinutes(2)))
                .aggregate(
                        () -> new FraudClient(0L, "", 0.0),
                        (key, fraudClient, total) -> {
                            total.setId(fraudClient.getId());
                            total.setClientName(fraudClient.getClientName());
                            total.setTotal(total.getTotal() + fraudClient.getTotal());
                            return total;
                        }
                );

        aggregationTable.toStream()
                .filter((key, value) -> value.getTotal() > 1000)
                .to("filtered-aggregation-topic",
                        Produced.with(WindowedSerdes.timeWindowedSerdeFrom(Long.class), CustomSerdes.fraudClient()));


    }
}
