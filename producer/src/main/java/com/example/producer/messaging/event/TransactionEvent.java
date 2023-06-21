package com.example.producer.messaging.event;

import com.example.producer.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TransactionEvent {
    private String bank;
    private Long clientId;
    private TransactionType transactionType;
    private Integer quantity;
    private Double price;
    private LocalDateTime createdAt;
}