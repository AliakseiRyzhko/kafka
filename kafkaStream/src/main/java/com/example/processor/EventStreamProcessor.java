package com.example.processor;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class EventStreamProcessor {
    @Autowired
    private StreamsBuilder streamsBuilder;

    @Value("${topic.create-client}")
    private String topicCreateClient;
    @PostConstruct
    public void streamTopology() {
      KStream<String,String> kStream = streamsBuilder.stream(topicCreateClient, Consumed.with(Serdes.String(), Serdes.String()));
        kStream.filter((key,value)-> value.startsWith("{")).mapValues((k, v) -> v.toUpperCase()).peek((k,v)-> System.out.println(k+v)).to("topic", Produced.with(Serdes.String(), Serdes.String()));
    }
}
