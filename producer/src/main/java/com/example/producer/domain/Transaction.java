package com.example.producer.domain;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class Transaction {
    private String bank;
    private Long clientId;
    private TransactionType transactionType;
    private Integer quantity;
    private Double price;
    private LocalDateTime createdAt;
}
