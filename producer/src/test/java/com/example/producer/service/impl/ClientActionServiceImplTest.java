package com.example.producer.service.impl;

import com.example.producer.domain.TransactionType;
import com.example.producer.messaging.event.ClientEvent;
import com.example.producer.messaging.event.TransactionEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@Testcontainers
class ClientActionServiceImplTest {

    public static final String TOPIC_NAME_SEND_CLIENT = "create.client";
    public static final String TOPIC_NAME_SEND_TRANSACTION = "create.transaction";

    @Autowired
    private ClientActionServiceImpl clientActionService;
    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.3")
    );

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }


    @Test
    void sendClient() {
        //given
        String bootstrapServers = kafka.getBootstrapServers();
        ClientEvent client = ClientEvent.builder()
                .id(1L)
                .clientName("fakeName")
                .email("fakeEmail").build();
        //when
        clientActionService.createClient(client);
        KafkaConsumer<String, ClientEvent> consumer = new KafkaConsumer<>(getProperties(bootstrapServers, ClientEvent.class));
        consumer.subscribe(Arrays.asList(TOPIC_NAME_SEND_CLIENT));
        ConsumerRecords<String, ClientEvent> records = consumer.poll(Duration.ofMillis(10000L));
        consumer.close();

        //then
        assertEquals(1, records.count());
        assertEquals(client.getId(), records.iterator().next().value().getId());
        assertEquals(client.getClientName(), records.iterator().next().value().getClientName());
        assertEquals(client.getEmail(), records.iterator().next().value().getEmail());

    }

    @Test
    void sendTransaction() {
        //given
        String bootstrapServers = kafka.getBootstrapServers();
        TransactionEvent transaction = TransactionEvent.builder()
                .bank("Prior")
                .clientId(1L)
                .transactionType(TransactionType.OUTCOME)
                .quantity(8)
                .price(5.5)
                .createdAt(LocalDateTime.of(2023, 05, 24, 17, 20, 00))
                .build();

        //when
        clientActionService.createTransaction(transaction);

        KafkaConsumer<String, TransactionEvent> consumer = new KafkaConsumer<>(getProperties(bootstrapServers, TransactionEvent.class));
        consumer.subscribe(Arrays.asList(TOPIC_NAME_SEND_TRANSACTION));
        ConsumerRecords<String, TransactionEvent> records = consumer.poll(Duration.ofMillis(10000L));
        consumer.close();

        //then
        assertEquals(1, records.count());
        assertEquals(transaction.getBank(), records.iterator().next().value().getBank());
        assertEquals(transaction.getClientId(), records.iterator().next().value().getClientId());
        assertEquals(transaction.getTransactionType(), records.iterator().next().value().getTransactionType());
        assertEquals(transaction.getQuantity(), records.iterator().next().value().getQuantity());
        assertEquals(transaction.getPrice(), records.iterator().next().value().getPrice());
        assertEquals(transaction.getCreatedAt(), records.iterator().next().value().getCreatedAt());
    }

    private Properties getProperties(String bootstrapServers, Class defaultType) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, defaultType);
        return properties;
    }
}