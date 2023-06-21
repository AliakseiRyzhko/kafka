package com.example.consumer.service.impl;

import com.example.consumer.domain.Client;
import com.example.consumer.domain.Transaction;
import com.example.consumer.exception.ClientNotFoundException;
import com.example.consumer.repository.ClientRepository;
import com.example.consumer.service.ClientService;
import com.example.consumer.service.messaging.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public void save(Client client) {
        Client savedClient = clientRepository.save(client);

        log.info("client saved {}", savedClient);
    }

    @Override
    public Client saveFakeClient(Long id) {
        Client client = Client.builder()
                .id(id)
                .clientName("fakeName")
                .email("fakeEmail")
                .transactions(Collections.emptyList())
                .isFakeClient(Boolean.TRUE).build();
        Client savedClient = clientRepository.save(client);
        log.info("fake client saved {}", savedClient);
        return savedClient;
    }

    @Override
    public boolean isExistClient(Long id) {
        log.info("Checking exist client");
        return clientRepository.existsById(id);
    }

    @Override
    public Client addTransactionToClient(TransactionEvent transactionEvent) {
        final Client client = getById(transactionEvent.getClientId());

        Transaction transaction = Transaction.builder()
                .bank(transactionEvent.getBank())
                .client(client)
                .transactionType(transactionEvent.getTransactionType())
                .quantity(transactionEvent.getQuantity())
                .price(transactionEvent.getPrice())
                .totalCost(new BigDecimal(transactionEvent.getQuantity() * transactionEvent.getPrice()))
                .createdAt(transactionEvent.getCreatedAt())
                .build();
        log.info("Add transaction to client");
        client.getTransactions().add(transaction);

        return client;
    }

    private Client getById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(
                        "Client with id: " + id + " not found"));
    }
}
