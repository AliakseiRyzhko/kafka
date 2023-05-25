package com.example.consumer.service.messaging.service;

import com.example.consumer.domain.dto.ClientDto;
import com.example.consumer.service.ClientService;
import com.example.consumer.service.messaging.event.ClientEvent;
import com.example.consumer.service.messaging.event.TransactionEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class MessagingService {
    private static final String topicCreateClient = "${topic.create-client}";
    private static final String topicCreateTransaction = "${topic.create-transaction}";
    private static final String clientGroupId = "${spring.kafka.consumer.group-client-id}";
    private static final String transactionGroupId = "${spring.kafka.consumer.group-transaction-id}";


    private final ClientService clientService;
    private final ModelMapper modelMapper;

    @Transactional
    @KafkaListener(topics = topicCreateClient, groupId = clientGroupId, containerFactory = "clientContainerFactory")
    public ClientEvent consumeClient(ClientEvent clientEvent) {
        log.info("client message consumed {}", clientEvent);
        clientService.save(modelMapper.map(clientEvent, ClientDto.class));
        return clientEvent;
    }

    @Transactional
    @KafkaListener(topics = topicCreateTransaction, groupId = transactionGroupId, containerFactory = "transactionContainerFactory")
    public TransactionEvent consumeTransaction(TransactionEvent transactionEvent) {
        log.info("transaction message consumed {}", transactionEvent);
        if (!clientService.isExistClient(transactionEvent.getClientId())) {
            clientService.saveFakeClient(transactionEvent.getClientId());
        }
        clientService.save(
                modelMapper.map(clientService.addTransactionToClient(transactionEvent), ClientDto.class));
        return transactionEvent;
    }
}
