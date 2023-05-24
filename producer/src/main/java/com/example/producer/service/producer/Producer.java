package com.example.producer.service.producer;

import com.example.producer.messaging.event.ClientEvent;
import com.example.producer.messaging.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Producer {


    @Value("${topic.create-client}")
    private String topicCreateClient;
    @Value("${topic.create-transaction}")
    private String topicCreateTransaction;
    private final KafkaTemplate<String, Object> clientKafkaTemplate;

    public String sendClientMessage(ClientEvent clientEvent) {
        clientKafkaTemplate.send(topicCreateClient, clientEvent);
        log.info("Client produced {}", clientEvent);
        return "client message sent";
    }

    public String sendTransactionMessage(TransactionEvent transactionEvent) {
        clientKafkaTemplate.send(topicCreateTransaction, transactionEvent);
        log.info("Client transaction produced {}", transactionEvent);
        return "transaction message sent";
    }
}
