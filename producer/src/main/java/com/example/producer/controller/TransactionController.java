package com.example.producer.controller;


import com.example.producer.domain.Transaction;
import com.example.producer.messaging.event.TransactionEvent;
import com.example.producer.service.ClientActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/transaction")
public class TransactionController {

    private final ClientActionService clientService;
    private final ModelMapper modelMapper;

    @PostMapping
    public String createTransaction(@RequestBody Transaction transaction) {
        log.info("create transaction request received for client {}", transaction.getClientId());
        return clientService.createTransaction(modelMapper.map(transaction, TransactionEvent.class));
    }
}