package com.example.consumer.service;

import com.example.consumer.domain.dto.TransactionDto;

public interface TransactionService {
    void save(TransactionDto transactionDto);
}
