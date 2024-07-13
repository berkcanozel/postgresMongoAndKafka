
package com.example.kafkaexample.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    @KafkaListener(topics = "example-topic", groupId = "group_id")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}
