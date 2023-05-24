package com.example.consumer.domain.dto;


import com.example.consumer.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private String bank;
    private Long clientId;
    private TransactionType transactionType;
    private Integer quantity;
    private Double price;
    private LocalDateTime createdAt;
}
