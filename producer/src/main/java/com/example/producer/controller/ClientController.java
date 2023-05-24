package com.example.producer.controller;


import com.example.producer.domain.Client;
import com.example.producer.messaging.event.ClientEvent;
import com.example.producer.service.ClientActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/client")
public class ClientController {

    private final ClientActionService clientService;
    private final ModelMapper modelMapper;

    @PostMapping
    public String createClient(@RequestBody Client client) {
        log.info("create client request received {}", client.getEmail());
        return clientService.createClient(modelMapper.map(client, ClientEvent.class));
    }
}