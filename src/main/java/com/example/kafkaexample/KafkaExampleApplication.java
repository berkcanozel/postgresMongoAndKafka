
package com.example.kafkaexample;

import com.example.kafkaexample.producer.MessageProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.kafkaexample.data.postgres.repository")
@EnableMongoRepositories(basePackages = "com.example.kafkaexample.data.mongo.repository")
@EnableScheduling
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
