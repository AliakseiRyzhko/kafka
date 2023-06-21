package com.example.consumer.service;

import com.example.consumer.domain.Transaction;

public interface TransactionService {
    void save(Transaction transaction);
}
