package com.example.consumer.service.messaging.service;

import com.example.consumer.service.messaging.event.ClientEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@Testcontainers
class MessagingServiceTest {

    public static final String TOPIC_NAME_SEND_CLIENT = "create.client";
    public static final String TOPIC_NAME_SEND_TRANSACTION = "create.transaction";

    @Autowired
    private MessagingService messagingService;

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.3")
    );


    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    void consumeClient() {
        //given
        String bootstrapServers = kafka.getBootstrapServers();
        ClientEvent client = ClientEvent.builder()
                .id(1L)
                .clientName("fakeName")
                .email("fakeEmail").build();
        //when
        KafkaProducer producer = new KafkaProducer<>(getProperties(bootstrapServers));
        producer.send(new ProducerRecord<>(TOPIC_NAME_SEND_CLIENT,client));
        producer.close();
        //Tests in progress
   //     messagingService.consumeClient(client);

    }

    @Test
    void consumeTransaction() {
    }

    private Properties getProperties(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return properties;
    }
}