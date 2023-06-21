package com.example.consumer.service.messaging.service;

import com.example.consumer.domain.Client;
import com.example.consumer.domain.Transaction;
import com.example.consumer.domain.TransactionType;
import com.example.consumer.repository.ClientRepository;
import com.example.consumer.repository.TransactionRepository;
import com.example.consumer.service.messaging.event.ClientEvent;
import com.example.consumer.service.messaging.event.TransactionEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessagingServiceTest {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public static final String TOPIC_NAME_SEND_CLIENT = "create.client";
    public static final String TOPIC_NAME_SEND_TRANSACTION = "create.transaction";
    public static final Long CLIENT_ID = 1L;

    @Container
    static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.3")
    );

    @Container
    static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:12")
            .withUsername("username")
            .withPassword("password")
            .withExposedPorts(5432)
            .withReuse(true);


    static {
        Startables.deepStart(Stream.of(POSTGRE_SQL_CONTAINER, KAFKA_CONTAINER)).join();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRE_SQL_CONTAINER::getDriverClassName);
    }

    @Test
    @Order(1)
    void consumeClient() throws InterruptedException {
        //given
        String bootstrapServers = KAFKA_CONTAINER.getBootstrapServers();
        ClientEvent client = ClientEvent.builder()
                .id(CLIENT_ID)
                .clientName("fakeName")
                .email("fakeEmail").build();
        //when
        KafkaProducer producer = new KafkaProducer<>(getProperties(bootstrapServers));
        TimeUnit.SECONDS.sleep(5);
        producer.send(new ProducerRecord<>(TOPIC_NAME_SEND_CLIENT, client));
        TimeUnit.SECONDS.sleep(5);
        //then
        Client clientFromDB = clientRepository.findById(CLIENT_ID).get();
        assertEquals(clientFromDB.getId(), CLIENT_ID);
        assertEquals(clientFromDB.getClientName(), client.getClientName());
        assertEquals(clientFromDB.getEmail(), client.getEmail());

    }

    @Test
    void consumeTransaction() throws InterruptedException {
        String bootstrapServers = KAFKA_CONTAINER.getBootstrapServers();
        final LocalDateTime TRANSACTION_CREATED_AT = LocalDateTime.of(2023, 05, 24, 17, 20, 00);
        TransactionEvent transaction = TransactionEvent.builder()
                .bank("Prior")
                .clientId(CLIENT_ID)
                .transactionType(TransactionType.OUTCOME)
                .quantity(8)
                .price(2.2)
                .createdAt(TRANSACTION_CREATED_AT)
                .build();
        //when
        KafkaProducer producer = new KafkaProducer<>(getProperties(bootstrapServers));
        TimeUnit.SECONDS.sleep(5);
        producer.send(new ProducerRecord<>(TOPIC_NAME_SEND_TRANSACTION, transaction));
        TimeUnit.SECONDS.sleep(5);
        //then
        Transaction transactionFromDB = transactionRepository.findAll().get(0);
        assertEquals(transactionFromDB.getBank(), transaction.getBank());
        assertEquals(transactionFromDB.getTransactionType(), transaction.getTransactionType());
        assertEquals(transactionFromDB.getQuantity(), transaction.getQuantity());
        assertEquals(transactionFromDB.getPrice(), transaction.getPrice());
        assertEquals(transactionFromDB.getCreatedAt(), transaction.getCreatedAt());
        assertEquals(transactionFromDB.getClient().getId(), CLIENT_ID);
    }

    private Properties getProperties(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return properties;
    }
}