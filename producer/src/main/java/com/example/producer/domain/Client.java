package com.example.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Client {
    private Long id;
    private String clientName;
    private String email;
}