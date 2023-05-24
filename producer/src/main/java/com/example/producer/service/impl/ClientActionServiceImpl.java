package com.example.producer.service.impl;

import com.example.producer.messaging.event.ClientEvent;
import com.example.producer.messaging.event.TransactionEvent;
import com.example.producer.service.ClientActionService;

import com.example.producer.service.producer.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientActionServiceImpl implements ClientActionService {
    private final Producer producer;
    private final ModelMapper modelMapper;

    @Override
    public String createClient(ClientEvent clientEvent) {
        return producer.sendClientMessage(clientEvent);
    }

    @Override
    public String createTransaction(TransactionEvent transactionEvent) {
        return producer.sendTransactionMessage(transactionEvent);
    }
}
