package com.example.nisum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.nisum.model.OrderResponse;

@Service
public class KafkaProducerService {

	private static final String TOPIC = "order";

    @Autowired
    private KafkaTemplate<String, OrderResponse> kafkaTemplate;

    public void sendMessage(OrderResponse orderResponse) {
        kafkaTemplate.send(TOPIC, orderResponse);
    }
}
