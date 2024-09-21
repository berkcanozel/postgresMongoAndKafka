
package com.example.kafkaexample;

import com.example.kafkaexample.producer.MessageProducer;
import com.example.kafkaexample.service.binance.BinanceNewService;
import com.example.kafkaexample.service.binance.BinanceWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
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

    @Autowired
    private BinanceNewService binanceService;

    @Autowired
    private BinanceWebSocketService binanceWebSocketService;


    public static void main(String[] args) {
        SpringApplication.run(KafkaExampleApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(MessageProducer producer) {
        return args -> {
            producer.sendMessage("Hello, Kafka!");
        };
    }

    @Bean
    public ApplicationRunner initializer() {
        return args -> {
            // Tarihsel veriyi çek
            System.out.println("Tarihsel veriyi çekmeye başlıyor...");
            binanceService.fetchAndStoreHistoricalData();
            System.out.println("Tarihsel veri çekme tamamlandı.");

            // WebSocket bağlantısını başlat
            System.out.println("WebSocket bağlantısını başlatıyor...");
            binanceWebSocketService.connectWebSocket();
            System.out.println("WebSocket bağlantısı başlatıldı.");
        };
    }
}
