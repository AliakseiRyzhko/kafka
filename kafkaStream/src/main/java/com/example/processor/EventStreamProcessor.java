package com.example.processor;

import com.example.domain.Client;
import com.example.domain.FraudClient;
import com.example.domain.Transaction;
import com.example.serdes.CustomSerdes;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
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
        KStream<String, Client> clientStream = streamsBuilder.stream(topicCreateClient, Consumed.with(Serdes.String(), CustomSerdes.client()));
        KStream<String, Transaction> transactionStream = streamsBuilder.stream(topicCreateTransaction, Consumed.with(Serdes.String(), CustomSerdes.transaction()));
        clientStream.join(transactionStream,(key, client, transaction) -> "left=" + client.getClientName() + ", right=" + transaction.getPrice(), JoinWindows.of(Duration.ofMinutes(2)));
        clientStream.filter((key,value)-> value.getClientName().startsWith("Ivan")).peek((k,v)-> System.out.println(k+v)).to("topic", Produced.with(Serdes.String(),  CustomSerdes.client()));
   }
}
