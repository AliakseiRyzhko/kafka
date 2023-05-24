package com.example.consumer.service;

import com.example.consumer.domain.Client;
import com.example.consumer.domain.dto.ClientDto;
import com.example.consumer.service.messaging.event.TransactionEvent;

public interface ClientService {
    void save(ClientDto clientDto);

    Client saveFakeClient(Long id);
    boolean isExistClient(Long id);
    Client addTransactionToClient(TransactionEvent transactionEvent);
}