
package com.example.kafkaexample.controller;

import com.example.kafkaexample.producer.MessageProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private final MessageProducer producer;

    public MessageController(MessageProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/publish")
    public String publishMessage(@RequestParam("message") String message) {
        producer.sendMessage(message);
        return "Message published";
    }
}
