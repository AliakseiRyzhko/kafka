package com.example.consumer.domain.dto;

import com.example.consumer.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto {
    private Long id;
    private String clientName;
    private String email;
    private Boolean isFakeClient;
    private List<Transaction> transactions;
}