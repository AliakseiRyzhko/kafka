package com.example.serdes;

import com.example.domain.Client;
import com.example.domain.FraudClient;
import com.example.domain.Transaction;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
public final class CustomSerdes {
    private CustomSerdes() {}
    public static Serde<Client> client() {
        ClientSerializer serializer = new ClientSerializer();
        ClientDeserializer deserializer = new ClientDeserializer();
        return Serdes.serdeFrom(serializer, deserializer);
    }
    public static Serde<FraudClient> fraudClient() {
        FraudClientSerializer serializer = new FraudClientSerializer();
        FraudClientDeserializer deserializer = new FraudClientDeserializer();
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<Transaction> transaction() {
        TransactionSerializer serializer = new TransactionSerializer();
        TransactionDeserializer deserializer = new TransactionDeserializer();
        return Serdes.serdeFrom(serializer, deserializer);
    }
}