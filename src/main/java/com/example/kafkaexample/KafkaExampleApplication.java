
package com.example.kafkaexample;

import com.example.kafkaexample.producer.MessageProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KafkaExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaExampleApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(MessageProducer producer) {
        return args -> {
            producer.sendMessage("Hello, Kafka!");
        };
    }
}
