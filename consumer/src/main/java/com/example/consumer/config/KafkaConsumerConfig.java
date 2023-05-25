package com.example.consumer.config;

import com.example.consumer.service.messaging.event.ClientEvent;
import com.example.consumer.service.messaging.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String boostrapServers;

    @Value(value = "${spring.kafka.consumer.group-client-id}")
    private String clientGroupId;

    @Value(value = "${spring.kafka.consumer.group-transaction-id}")
    private String transactionGroupId;

    public Map<String, Object> clientConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, clientGroupId);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }

    public Map<String, Object> transactionConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, transactionGroupId);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }

    @Bean
    public ConsumerFactory<String, ClientEvent> clientConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(clientConsumerConfigs(), new StringDeserializer(), new JsonDeserializer<>(ClientEvent.class, false));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ClientEvent>> clientContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, ClientEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(clientConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TransactionEvent> transactionConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(transactionConsumerConfigs(), new StringDeserializer(), new JsonDeserializer<>(TransactionEvent.class, false));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, TransactionEvent>> transactionContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, TransactionEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionConsumerFactory());
        return factory;
    }

}
