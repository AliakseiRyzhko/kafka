package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Slf4j
@SpringBootApplication
@EnableKafka
@EnableKafkaStreams
public class KafkaStreams {

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreams.class, args);
        log.info("Running stream app...");
    }

}
