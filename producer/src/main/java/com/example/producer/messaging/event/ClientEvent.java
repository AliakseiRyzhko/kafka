package com.example.producer.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientEvent {
    private Long id;
    private String clientName;
    private String email;
}
