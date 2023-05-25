package com.example.producer.service;

import com.example.producer.messaging.event.ClientEvent;
import com.example.producer.messaging.event.TransactionEvent;

public interface ClientActionService {
    String createClient(ClientEvent clientEvent);

    String createTransaction(TransactionEvent transactionEvent);
}