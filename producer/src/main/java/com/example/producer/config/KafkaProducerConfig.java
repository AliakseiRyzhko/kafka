package com.example.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {
    @Value("${topic.create-client}")
    private String topicCreateClient;
    @Value("${topic.create-transaction}")
    private String topicCreateTransaction;

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
}
