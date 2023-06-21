package com.example.consumer.service.impl;

import com.example.consumer.domain.Transaction;
import com.example.consumer.repository.TransactionRepository;
import com.example.consumer.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    @Override
    public void save(Transaction transaction) {
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("client transaction saved {}", savedTransaction);
    }
}
