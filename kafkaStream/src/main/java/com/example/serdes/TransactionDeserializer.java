package com.example.serdes;

import com.example.domain.Transaction;
import com.google.gson.Gson;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.Closeable;
import java.nio.charset.Charset;
import java.util.Map;
public class TransactionDeserializer implements Closeable, AutoCloseable, Deserializer<Transaction> {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    static private Gson gson = new Gson();

    @Override
    public void configure(Map<String, ?> map, boolean b) {
    }

    @Override
    public Transaction deserialize(String topic, byte[] bytes) {
        try {
            // Transform the bytes to String
            String transaction = new String(bytes, CHARSET);
            // Return the Transaction object created from the String 'transaction'
            return gson.fromJson(transaction, Transaction.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error reading bytes", e);
        }
    }

    @Override
    public void close() {

    }
}