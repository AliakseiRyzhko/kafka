package com.example.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${topic.create-client}")
    private String topicCreateClient;
    @Value("${topic.create-transaction}")
    private String topicCreateTransaction;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> clientProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getKafkaConfigProps());
    }

    @Bean
    public KafkaTemplate<String, Object> clientKafkaTemplate() {
        return new KafkaTemplate<>(clientProducerFactory());
    }

    @Bean
    public NewTopic clientTopic() {
        return TopicBuilder
                .name(topicCreateClient)
                .partitions(5)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic transactionTopic() {
        return TopicBuilder
                .name(topicCreateTransaction)
                .partitions(5)
                .replicas(1)
                .build();
    }

    private Map<String, Object> getKafkaConfigProps() {
        final Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }
}
